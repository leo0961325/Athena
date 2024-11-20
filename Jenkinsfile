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
              withAWS(credentials: 'aws-credentials', region: env.AWS_REGION) {
                  sh '''
                      gradle jib \
                          -Penv=${ENV} \
                          -Prepo=${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com
                  '''
              }
          }
       }

       stage('Deploy CloudFormation') {
          steps {
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
          }
       }
    }
}