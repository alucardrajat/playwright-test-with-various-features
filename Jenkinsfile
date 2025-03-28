pipeline {
    agent any
    
    tools {
        maven 'Maven 3.8.6'
        jdk 'JDK 17'
    }
    
    environment {
        MAVEN_OPTS = '-Xmx2048m -Xms512m'
        BROWSER = 'chrome'
        HEADLESS = 'true'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Install Dependencies') {
            steps {
                sh 'mvn clean install -DskipTests'
                sh 'mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"'
            }
        }
        
        stage('Run Tests') {
            steps {
                sh 'mvn test -Dtest=${TEST_SUITE:-"HomePageTests"} -Dbrowser=${BROWSER} -Dheadless=${HEADLESS}'
            }
        }
        
        stage('Archive Results') {
            steps {
                archiveArtifacts artifacts: 'reports/**/*,test-results/**/*', fingerprint: true
                publishHTML(target: [
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'reports',
                    reportFiles: 'TestExecutionReport.html',
                    reportName: 'Test Execution Report'
                ])
            }
        }
        
        stage('Cleanup') {
            steps {
                cleanWs()
            }
        }
    }
    
    post {
        always {
            emailext (
                subject: "Build ${currentBuild.result}: Job ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
                body: """
                    <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>
                    <p>Test Report: <a href='${env.BUILD_URL}HTML_20Report/'>Test Execution Report</a></p>
                """,
                recipientProviders: [[$class: 'DevelopersRecipientProvider']]
            )
        }
        
        success {
            echo "Build succeeded!"
        }
        
        failure {
            echo "Build failed!"
        }
    }
} 