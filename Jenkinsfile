pipeline {
    agent any

    parameters {
        choice(name: 'ENV', choices: ['dev', 'prod'], description: '選擇部署環境')
        string(name: 'COMMIT_HASH', defaultValue: '', description: 'Commit Hash')
    }

    environment {
        AWS_CREDENTIALS = credentials('aws-credentials')
        AWS_ACCOUNT_ID = '194722439964'
        AWS_REGION = 'ap-southeast-1'
        ECR_REPO_NAME = 'athena'
    }

    stages {
        stage('Environment Check') {
            steps {
                sh '''
                    aws --version
                    docker --version
                    ./gradlew --version
                '''
            }
        }

        stage('Template Validation') {
            steps {
                script {
                    try {
                        sh """
                            pwd
                            ls -la
                            echo "Looking for template: ${params.ENV}-template.yml"
                            if [ ! -f "${params.ENV}-template.yml" ]; then
                                echo "Template file not found!"
                                exit 1
                            fi
                            aws cloudformation validate-template --template-body file://${params.ENV}-template.yml
                        """
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        error "Template 驗證失敗: ${e.getMessage()}"
                    }
                }
            }
        }

        stage('Create ECR Repository') {
            steps {
                script {
                    try {
                        withAWS(credentials: 'aws-credentials', region: env.AWS_REGION) {
                            sh '''
                                aws ecr describe-repositories --repository-names ${ECR_REPO_NAME} || \
                                aws ecr create-repository --repository-name ${ECR_REPO_NAME} \
                                    --image-scanning-configuration scanOnPush=true \
                                    --encryption-configuration encryptionType=AES256
                            '''
                        }
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        error "ECR Repository 建立失敗: ${e.getMessage()}"
                    }
                }
            }
        }

        stage('ECR Login') {
            steps {
                script {
                    try {
                        withAWS(credentials: 'aws-credentials', region: env.AWS_REGION) {
                            sh '''
                                aws ecr get-login-password --region ${AWS_REGION} | \
                                docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com
                            '''
                        }
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        error "ECR 登入失敗: ${e.getMessage()}"
                    }
                }
            }
        }

        stage('Build & Push') {
            steps {
                script {
                    try {
                        withAWS(credentials: 'aws-credentials', region: env.AWS_REGION) {
                            sh '''
                                ./gradlew jib \
                                    -Penv=${ENV} \
                                    -Prepo=${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO_NAME} \
                                    --stacktrace
                            '''
                        }
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        error "建置與推送映像失敗: ${e.getMessage()}"
                    }
                }
            }
        }

        stage('Cleanup Failed Stack') {
            steps {
                withAWS(credentials: 'aws-credentials', region: env.AWS_REGION) {
                    sh """
                        if aws cloudformation describe-stacks --stack-name api-stack-${params.ENV} 2>/dev/null | grep 'ROLLBACK_COMPLETE'; then
                            aws cloudformation delete-stack --stack-name api-stack-${params.ENV}
                            aws cloudformation wait stack-delete-complete --stack-name api-stack-${params.ENV}
                        fi
                    """
                }
            }
        }

        stage('Deploy CloudFormation') {
            steps {
                script {
                    try {
                        withAWS(credentials: 'aws-credentials', region: env.AWS_REGION) {

//                             sh """
//                                 echo "Deploying stack: api-stack-${params.ENV}"
//                                 aws cloudformation deploy \
//                                     --template-file ${params.ENV}-template.yml \
//                                     --stack-name api-stack-${params.ENV} \
//                                     --parameter-overrides \
//                                         CommitHash=${params.COMMIT_HASH} \
//                                         Environment=${params.ENV} \
//                                     --capabilities CAPABILITY_NAMED_IAM \
//
//
//                                 echo "Checking deployment status..."
//                                 aws cloudformation describe-stacks \
//                                     --stack-name api-stack-${params.ENV} \
//                                     --query 'Stacks[0].StackStatus' \
//                                     --output text
//                             """
//                             // 更新 ECS service
//                            sh  """
//                                aws ecs update-service \
//                                    --cluster api-cluster \
//                                    --service api-service \
//                                    --force-new-deployment
//                                """
                            sh """
                                            aws ecs update-service \
                                                --cluster api-cluster \
                                                --service api-service \
                                                --force-new-deployment \
                                                --task-definition $(aws ecs describe-task-definition \
                                                    --task-definition api-service \
                                                    --query 'taskDefinition.taskDefinitionArn' \
                                                    --output text)
                                        """

                                        sh """
                                            aws cloudformation deploy \
                                                --template-file ${params.ENV}-template.yml \
                                                --stack-name api-stack-${params.ENV} \
                                                --parameter-overrides \
                                                    CommitHash=${params.COMMIT_HASH} \
                                                    Environment=${params.ENV} \
                                                    DevIP=0.0.0.0/0 \
                                                    BuildNumber=${BUILD_NUMBER} \
                                                --capabilities CAPABILITY_NAMED_IAM
                                        """
                        }
                    } catch (Exception e) {
                        sh """
                            echo "Fetching stack events..."
                            aws cloudformation describe-stack-events \
                                --stack-name api-stack-${params.ENV} \
                                --query 'StackEvents[?ResourceStatus==`CREATE_FAILED`]'
                        """
                        currentBuild.result = 'FAILURE'
                        error "CloudFormation部署失敗: ${e.getMessage()}"
                    }
                }
            }
        }
    }

    post {
        failure {
            echo 'Pipeline執行失敗!'
        }
        success {
            echo 'Pipeline執行成功!'
        }
        always {
            script {
                sh """
                    echo "清理 Docker 登入憑證..."
                    rm -f ~/.docker/config.json || true
                """
                cleanWs()
            }
        }
    }
}