def NOTIFY_USERS = 'hello@gmail.com'
def DOCKER_IMG_NAME = 'wallaceww/my-docker-repo'
def DOCKER_IMG_TAG = '1.0.0'
def LOCAL_SERVER = 'localhost'
def REMOTE_SERVER = '192.168.1.4'

pipeline {
    agent any

    tools {
        maven 'maven 3.9.8'
        jdk 'OpenJDK17'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '1'))
        disableConcurrentBuilds(abortPrevious: true)
    }

    parameters {
        choice(name: 'PIPELINE_ENV', choices: ['Docker', 'Local'], description: 'Run pipeline in either local or docker environment')
        booleanParam(name: 'BUILD_DOCKER_IMAGE', defaultValue: true, description: 'Build Docker Image for production')
        string(name: 'EMAIL_LIST', defaultValue: "${NOTIFY_USERS}", description: 'Email notifications to')
    }

    stages {
        stage('Clean') {
            steps {
                sh '''
                    echo "Start clean"
                    mvn clean
                '''
            }
        }

        stage('Test') {
            steps {
                sh '''
                    echo "Start test"
                    mvn install
                '''
            }
        }

        stage('Sonar') {
            steps {
                withSonarQubeEnv(installationName: 'sonarqube-local') {
                    sh '''
                        echo "Performing sonar"
                        mvn sonar:sonar
                    '''
                }
                script {
                    def qualitygate = waitForQualityGate()
                    if (qualitygate.status != 'OK') {
                        error "Pipeline aborted due to quality gate coverage failure: ${qualitygate.status}"
                    }
                }
            }
        }

        stage('Build') {
            steps {
                sh '''
                    echo "Start build"
                    mvn install -DskipTests
                '''
                archiveArtifacts artifacts: 'target/*.war'
            }
        }

        stage('Build Docker Image') {
            when {
                expression {
                    params.BUILD_DOCKER_IMAGE == true
                }
            }
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'dockerHubPassword', usernameVariable: 'dockerHubUser')]) {
                    sh """
                        echo "Building docker image"
                        docker build -t ${DOCKER_IMG_NAME}:${DOCKER_IMG_TAG} .
                        echo ${env.dockerHubPassword} | docker login -u ${env.dockerHubUser} --password-stdin 
                        docker push ${DOCKER_IMG_NAME}:${DOCKER_IMG_TAG}
                        docker logout
                    """
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    if (params.PIPELINE_ENV == 'Local') {
                            deploy adapters: [tomcat9(url: "http://${LOCAL_SERVER}:8888", credentialsId: 'tomcat')],
                                war: 'target/*.war',
                                contextPath: 'java-web-app'
                    } else {
                        deploy adapters: [tomcat9(url: "http://${REMOTE_SERVER}:8888", credentialsId: 'tomcat')],
                            war: 'target/*.war',
                            contextPath: 'java-web-app'
                    }
                }
            }
        }
    }

    post {
        failure {
            mail to: "${params.EMAIL_LIST}",
                subject: "${env.JOB_NAME} - Build #${env.BUILD_NUMBER} Failed!",
                body: """
                    <p>Check console output at <a href="${env.BUILD_URL}">here</a> to view the results.</p>
                """,
                mimeType: 'text/html'
        }

        success {
            mail to: "${params.EMAIL_LIST}",
                subject: "${env.JOB_NAME} - Build #${env.BUILD_NUMBER} Succeeded!",
                body: """
                    <p>Check console output at <a href="${env.BUILD_URL}">here</a> to view the results.</p>
                """,
                mimeType: 'text/html'
            
            script {
                if (params.PIPELINE_ENV == 'Local') {
                    echo 'Trigger selenium pipeline tests'
                    build job: 'selenium-java-web-app', propagate: false, wait: false
                }
            }
        }
    }
}