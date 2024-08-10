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
        booleanParam(name: 'SONAR_QUALITY_GATE', defaultValue: false, description: 'Enable overall code quality check')
        booleanParam(name: 'BUILD_DOCKER_IMAGE', defaultValue: true, description: 'Build Docker Image for production')
        string(name: 'EMAIL_LIST', defaultValue: "${NOTIFY_USERS}", description: 'Email notifications to')
    }

    stages {
        stage('clean') {
            steps {
                sh '''
                    echo "Start clean"
                    mvn clean
                '''
            }
        }

        stage('test') {
            steps {
                sh '''
                    echo "Start test"
                    mvn install
                '''
            }
        }

        stage('sonar') {
            steps {
                withSonarQubeEnv(installationName: 'sonarqube-local') {
                    sh '''
                        echo "Performing sonar"
                        mvn sonar:sonar
                    '''
                }
            }
        }

        stage('sonar quality gate') {
            when {
                anyOf {
                    branch 'develop';
                    triggeredBy 'SONAR_QUALITY_GATE'
                }
            }

            steps {
                script {
                    def qualitygate = waitForQualityGate()
                    if (qualitygate.status != 'OK') {
                        error "Pipeline aborted due to quality gate coverage failure: ${qualitygate.status}"
                    }
                }
            }
        }

        stage('build') {
            steps {
                sh '''
                    echo "Start build"
                    mvn install -DskipTests
                '''
                archiveArtifacts artifacts: 'target/*.war'
            }
        }

        stage('Build Docker Image') {
            // when {
            //     triggeredBy 'BUILD_DOCKER_IMAGE'
            // }
            steps {
                sh '''
                    echo "Building docker image"
                    docker build -t java-web-app:1.0.0 .
                '''
            }
        }

        stage('deploy') {
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