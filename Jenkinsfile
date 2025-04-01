pipeline {
    agent any
    
    tools {
        maven 'Maven_3.9.9'
        jdk 'JDK 17'
    }
    
    parameters {
        choice(name: 'TEST_SUITE', choices: ['HomePageTests', 'LoginPageTest', 'SearchTest', 'BannerSolutionsTest'], description: 'Select Test Suite to run')
        string(name: 'TEST_METHODS', defaultValue: '', description: 'Comma-separated test methods to run (leave empty to run all tests in suite)')
        choice(name: 'BROWSER', choices: ['chrome', 'firefox', 'webkit'], description: 'Browser to run tests')
        booleanParam(name: 'HEADLESS', defaultValue: true, description: 'Run tests in headless mode')
        string(name: 'PARALLEL_COUNT', defaultValue: '3', description: 'Number of parallel threads')
    }
    
    environment {
        MAVEN_OPTS = '-Xmx2048m -Xms512m'
        JAVA_HOME = tool 'JDK 17'
        PATH = "${JAVA_HOME}/bin:${PATH}"
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
                        def testMethods = params.TEST_METHODS ? params.TEST_METHODS.split(',').collect { it.trim() } : []
                        def testCommand = buildTestCommand(params.TEST_SUITE, testMethods, params.BROWSER, params.HEADLESS, params.PARALLEL_COUNT)
                        
                        echo "Executing test command: ${testCommand}"
                        sh testCommand
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
                def recipientEmails = 'your.email@example.com'  // Replace with your email
                emailext (
                    to: recipientEmails,
                    subject: "Build ${currentBuild.result}: Job ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
                    body: """
                        <html>
                        <body>
                            <h2>Build Status: ${currentBuild.result}</h2>
                            <h3>Build Information</h3>
                            <ul>
                                <li>Build Number: ${env.BUILD_NUMBER}</li>
                                <li>Test Suite: ${params.TEST_SUITE}</li>
                                <li>Test Methods: ${params.TEST_METHODS ?: 'All'}</li>
                                <li>Browser: ${params.BROWSER}</li>
                                <li>Parallel Threads: ${params.PARALLEL_COUNT}</li>
                                <li>Build URL: <a href='${env.BUILD_URL}'>${env.JOB_NAME}</a></li>
                                <li>Test Report: <a href='${env.BUILD_URL}HTML_20Report/'>View Test Report</a></li>
                            </ul>
                        </body>
                        </html>
                    """,
                    mimeType: 'text/html',
                    recipientProviders: [[$class: 'DevelopersRecipientProvider']],
                    attachLog: true
                )
            }
        }
    }
}

def buildTestCommand(testSuite, testMethods, browser, headless, threadCount) {
    def command = "mvn test"
    
    // Add test suite
    command += " -Dtest=${testSuite}"
    
    // Add specific test methods if provided
    if (testMethods) {
        def methodsList = testMethods.collect { "${testSuite}#${it}" }.join(',')
        command += "#${methodsList}"
    }
    
    // Add browser configuration
    command += " -Dbrowser=${browser}"
    command += " -Dheadless=${headless}"
    
    // Add parallel execution configuration
    command += " -DthreadCount=${threadCount}"
    command += " -Dparallel=methods"
    
    return command
} 