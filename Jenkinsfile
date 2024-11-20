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
    }

    stages {
        stage('Build & Push') {
            steps {
                script {
                    try {
                        withAWS(credentials: 'aws-credentials', region: env.AWS_REGION) {
                            sh '''
                                gradle jib \
                                    -Penv=${ENV} \
                                    -Prepo=${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com
                            '''
                        }
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        error "建置與推送映像失敗: ${e.getMessage()}"
                    }
                }
            }
        }

        stage('Deploy CloudFormation') {
            steps {
                script {
                    try {
                        withAWS(credentials: 'aws-credentials', region: env.AWS_REGION) {
                            sh """
                                aws cloudformation deploy \
                                    --template-file ${params.ENV}-template.yml \
                                    --stack-name api-stack-${params.ENV} \
                                    --parameter-overrides \
                                        CommitHash=${params.COMMIT_HASH} \
                                    --capabilities CAPABILITY_NAMED_IAM
                            """
                        }
                    } catch (Exception e) {
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
            cleanWs()
        }
    }
}