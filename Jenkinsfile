pipeline {
    agent any
    
    tools {
        maven 'Maven_3.9.9'
        jdk 'JDK 17'
    }
    
    environment {
        MAVEN_OPTS = '-Xmx2048m -Xms512m'
        JAVA_HOME = tool 'JDK 17'
        PATH = "${JAVA_HOME}/bin:${PATH}"
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
                sh '''
                    echo "Java version:"
                    java -version
                    echo "Maven version:"
                    mvn -version
                    mvn clean install -DskipTests
                    mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
                '''
            }
        }
        
        stage('Run Tests') {
            steps {
                script {
                    try {
                        sh 'mvn test -Dtest=${TEST_SUITE:-"HomePageTests"} -Dbrowser=${BROWSER} -Dheadless=${HEADLESS}'
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        error("Test execution failed: ${e.message}")
                    }
                }
            }
        }
        
        stage('Archive Results') {
            steps {
                archiveArtifacts artifacts: 'reports/**/*,test-results/**/*', fingerprint: true, allowEmptyArchive: true
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
            script {
                def recipientEmails = 'rajatkumar@gmail.com'  // Replace with your email
                emailext (
                    to: recipientEmails,
                    subject: "Build ${currentBuild.result}: Job ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
                    body: """
                        <p>Build Status: ${currentBuild.result}</p>
                        <p>Build Number: ${env.BUILD_NUMBER}</p>
                        <p>Check console output at: <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a></p>
                        <p>Test Report: <a href='${env.BUILD_URL}HTML_20Report/'>Test Execution Report</a></p>
                    """,
                    mimeType: 'text/html',
                    recipientProviders: [[$class: 'DevelopersRecipientProvider']]
                )
            }
        }
        
        success {
            echo "Build succeeded!"
        }
        
        failure {
            echo "Build failed!"
        }
    }
} 