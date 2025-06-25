pipeline {
    agent {
        docker {
            image 'pilet/jenkins-cicd:1.2'
            args '-v /var/run/docker.sock:/var/run/docker.sock -u 0:0'
        }
    }
    stages {

        stage('Build') {
            steps {
                sh '''
                    mvn -version
                    mvn clean package -q
                    ls -all target
                '''
            }
        }

        stage('Deploy') {
            environment {
                DOCKER_LOGIN = credentials('DOCKER_LOGIN')
                KUBECONFIG = credentials('KUBECONFIG')
            }
            steps {
                script {
                    env.VERSION = sh (
                        script: "cat pom.xml | grep '<version>' | head -n 2 | tail -n 1 | awk -F'[><]' '{print \$3}'",
                        returnStdout: true)
                    sh '''
                        echo "Version: $VERSION"
                        dockerd &
                        echo $DOCKER_LOGIN_PSW | docker login -u $DOCKER_LOGIN_USR --password-stdin
                        docker build -t pilet/kueb-workflow:$VERSION .
                        docker push pilet/kueb-workflow:$VERSION

                        # cat $KUBECONFIG
                        # kubectl create ns apps --dry-run=client -o yaml | kubectl apply --kubeconfig $KUBECONFIG -f -
                        # cat kubernetes/deployment.yaml | POM_VERSION=${VERSION} envsubst | kubectl apply -n apps --kubeconfig $KUBECONFIG -f -
                    '''
                }
            }
        }
    }

}