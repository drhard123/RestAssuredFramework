pipeline {

    agent any

    // Tools configured in Jenkins → Manage Jenkins → Tools
    tools {
        maven 'Maven-3.9.14'
        allure 'Allure-2.33'
    }

    // Environment variables — credentials never hardcoded
    environment {
        JAVA_HOME    = tool 'JDK-17'
        PROJECT_NAME = 'REST Assured API Automation Framework'
    }

    // Pipeline triggers — runs automatically on code push
    triggers {
        // Poll GitHub every 5 minutes for new commits
        pollSCM('H/5 * * * *')
    }

    stages {

        // ── Stage 1: Checkout code from GitHub ──
        stage('Checkout') {
            steps {
                echo '═══ Checking out source code ═══'
                checkout scm
                echo "Branch: ${env.GIT_BRANCH}"
                echo "Commit: ${env.GIT_COMMIT}"
            }
        }

        // ── Stage 2: Compile the project ──
        stage('Build') {
            steps {
                echo '═══ Compiling project ═══'
                sh 'mvn clean compile -q'
            }
        }

        // ── Stage 3: Run all API tests ──
        stage('Run Tests') {
            steps {
                echo '═══ Running API test suite ═══'
                sh 'mvn test'
            }
            // Always collect TestNG results even if tests fail
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        // ── Stage 4: Generate Allure report ──
        stage('Generate Report') {
            steps {
                echo '═══ Generating Allure report ═══'
                allure([
                    includeProperties: false,
                    jdk              : '',
                    results          : [[path: 'allure-results']],
                    reportBuildPolicy: 'ALWAYS',
                    report           : 'allure-report'
                ])
            }
        }
    }

    // ── Post-pipeline actions ──
    post {

        success {
            echo '✅ Pipeline passed — all tests green'
            emailext(
                subject : "✅ PASSED: ${PROJECT_NAME} — Build #${BUILD_NUMBER}",
                body    : """
                    <h2>Build Passed</h2>
                    <p><b>Project:</b> ${PROJECT_NAME}</p>
                    <p><b>Build:</b>   #${BUILD_NUMBER}</p>
                    <p><b>Branch:</b>  ${env.GIT_BRANCH}</p>
                    <p><b>Duration:</b>${currentBuild.durationString}</p>
                    <p>
                      <a href='${BUILD_URL}allure'>
                        View Allure Report
                      </a>
                    </p>
                """,
                mimeType: 'text/html',
                to      : 'your-email@gmail.com'
            )
        }

        failure {
            echo '❌ Pipeline failed — check test results'
            emailext(
                subject : "❌ FAILED: ${PROJECT_NAME} — Build #${BUILD_NUMBER}",
                body    : """
                    <h2>Build Failed</h2>
                    <p><b>Project:</b> ${PROJECT_NAME}</p>
                    <p><b>Build:</b>   #${BUILD_NUMBER}</p>
                    <p><b>Branch:</b>  ${env.GIT_BRANCH}</p>
                    <p><b>Error:</b>   ${currentBuild.description}</p>
                    <p>
                      <a href='${BUILD_URL}console'>
                        View Console Log
                      </a>
                    </p>
                """,
                mimeType: 'text/html',
                to      : 'your-email@gmail.com'
            )
        }

        always {
            echo '═══ Archiving logs ═══'
            archiveArtifacts artifacts: 'logs/*.log',
                             allowEmptyArchive: true
            cleanWs()
        }
    }
}
