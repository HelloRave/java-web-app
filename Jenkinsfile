def NOTIFY_USERS = 'hello@gmail.com'

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
                triggeredBy 'BUILD_DOCKER_IMAGE'
            }
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'dockerHubPassword', usernameVariable: 'dockerHubUser')]) {
                    sh """
                        echo "Building docker image"
                        docker build -t wallaceww/my-docker-repo:1.0.0 .
                        echo ${env.dockerHubPassword} | docker login -u ${env.dockerHubUser} --password-stdin 
                        docker push wallaceww/my-docker-repo:1.0.0
                        docker logout
                    """
                }
            }
        }

        stage('Deploy') {
            steps {
                deploy adapters: [tomcat9(url: 'http://192.168.1.4:8888', credentialsId: 'tomcat')],
                    war: 'target/*.war',
                    contextPath: 'java-web-app'
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
        }
    }
}