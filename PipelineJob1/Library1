def call(Map pipelineParams) {
    pipeline {
        agent any
        stages {
            stage ("Request website") {
                steps{
                    script{
                        response_code = sh (
                            script: "curl -o /dev/null -s -w %{http_code} ${pipelineParams.domain}",
                            returnStdout: true
                        ).toInteger()
                        
                        echo "${response_code}"                    
                    }

                }
            }
            stage ("Check response code") {
                steps{
                    script{
                        if ( response_code != pipelineParams.code ){
                            sh "false"
                        }             
                    }

                }
            }
        }
        post {
            success{
                echo "success"
            }
            failure{
                script {
                    withCredentials([string(credentialsId: 'ithome-telegram-bot-token', variable: 'TOKEN')]){
                        withCredentials([string(credentialsId: 'ithome-telegram-notification-group', variable: 'GROUP_ID')]){
                            sh """#!/bin/bash
                                curl -X GET https://api.telegram.org/bot${TOKEN}/sendMessage -d "chat_id=${GROUP_ID}&text=\"\"${pipelineParams.domain} response code != ${pipelineParams.code}.\"\""
                            """
                        }
                    }
                }
            }
            
        }
    }

}
