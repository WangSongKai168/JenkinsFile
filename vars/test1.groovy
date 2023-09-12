// vars/testIthome.groovy

def call(String executor){
    script {
        if ( executor == "Ithome" ){
            echo "Hi Ben"
        } else if (executor == "Jenkins" ){
            echo "Hi Jenkins"
        } else {
            echo "unrecognizable"
        }
    }
}
