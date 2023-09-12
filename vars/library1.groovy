def call(Map args) {
    pipeline {
        agent any
        stages {
            stage ("Stage1: Request website") {
                steps{
                    script{
                        response_code = sh (
                            script: "curl -o /dev/null -s -w %{http_code} ${args.domain}",
                            returnStdout: true
                        ).toInteger()
                        
                        echo "${response_code}"                    
                    }
                }
            }
            stage ("Stage2: Check response code") {
                steps{
                    script{
                        if ( response_code != ${args.statuscode} ){
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
                            def domain = args.domain
                            sh '''
                                curl -X POST -H 'Content-Type: application/json' -d '{"Domain": "$domain", "StatusCode": "SUCCESS"}' http://aspnetcore/api/Alert/AlertWebhook
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
                            def domain = args.domain
                            sh '''
                                curl -X POST -H 'Content-Type: application/json' -d '{"Domain": "$domain", "StatusCode": "FAILURE"}' http://aspnetcore/api/Alert/AlertWebhook
                            '''
                        }
                    }
                }
            }
            
        }
    }
}
