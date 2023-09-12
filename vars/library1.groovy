def call(Map pipelineParams) {
pipeline {
    agent any
    stages {
        stage ("Stage1: Request website") {
            steps{
                script{
                    response_code = sh (
                        script: "curl -o /dev/null -s -w %{http_code} www.example.com",
                        returnStdout: true
                    ).toInteger()
                    
                    echo "${response_code}"                    
                }
            }
        }
        stage ("Stage2: Check response code") {
            steps{
                script{
                    if ( response_code != 200 ){
                        sh "false"
                    }             
                }

            }
        }
    }
    post {
        success{
            echo "success"
            script {
                withCredentials([string(credentialsId: 'TOKEN', variable: 'TOKEN')]){
                    withCredentials([string(credentialsId: 'GROUP_ID', variable: 'GROUP_ID')]){
                        url = 'http://aspnetcore/api/Alert/AlertWebhook'
                        jsonBody = '{"StatusCode": "SUCCESS"}'
                        sh '''
                            curl -X POST -H 'Content-Type: application/json' -d '${jsonBody}' ${url}
                        '''
                    }
                }
            }
        }
        failure{
            echo "failure"          
            script {
                withCredentials([string(credentialsId: 'TOKEN', variable: 'TOKEN')]){
                    withCredentials([string(credentialsId: 'GROUP_ID', variable: 'GROUP_ID')]){
                        url = 'http://aspnetcore/api/Alert/AlertWebhook'
                        jsonBody = '{"StatusCode": "FAILURE"}'
                        sh '''
                            curl -X POST -H 'Content-Type: application/json' -d '${jsonBody}' ${url}
                        '''
                    }
                }
            }
        }
        
    }
}
}
