pipeline {
    agent any
    
    // Build parameters for demonstration
    parameters {
        choice(
            name: 'BUILD_ENVIRONMENT',
            choices: ['development', 'staging', 'production'],
            description: 'Target environment for the build'
        )
        string(
            name: 'BUILD_VERSION',
            defaultValue: '1.0.0',
            description: 'Version number for the build'
        )
        booleanParam(
            name: 'RUN_TESTS',
            defaultValue: true,
            description: 'Whether to run tests'
        )
        booleanParam(
            name: 'SKIP_SONAR',
            defaultValue: false,
            description: 'Skip SonarQube analysis'
        )
        string(
            name: 'DEPLOY_SERVER',
            defaultValue: 'server.example.com',
            description: 'Target deployment server'
        )
    }
    
    // Environment variables
    environment {
        // Non-sensitive environment variables
        APP_NAME = 'demo-spring-boot-app'
        JAVA_HOME = tool 'JDK-21'
        GRADLE_OPTS = '-Dorg.gradle.daemon=false'
        BUILD_TIMESTAMP = sh(script: "date '+%Y%m%d-%H%M%S'", returnStdout: true).trim()
        
        // Derived environment variable from parameter
        APP_ENVIRONMENT = "${params.BUILD_ENVIRONMENT}"
        APP_VERSION = "${params.BUILD_VERSION}"
        
        // Sensitive environment variables (using credentials)
        DB_PASSWORD = credentials('database-password')
        API_KEY = credentials('api-key')
        SONAR_TOKEN = credentials('sonar-token')
        DEPLOY_SSH_KEY = credentials('deploy-ssh-key')
    }
    
    options {
        // Keep last 10 builds
        buildDiscarder(logRotator(numToKeepStr: '10'))
        
        // Timeout for the entire pipeline
        timeout(time: 30, unit: 'MINUTES')
        
        // Disable concurrent builds
        disableConcurrentBuilds()
        
        // Add timestamps to console output
        timestamps()
    }
    
    stages {
        stage('Initialization') {
            steps {
                echo '========== Build Initialization =========='
                echo "Environment: ${APP_ENVIRONMENT}"
                echo "Version: ${APP_VERSION}"
                echo "Build Timestamp: ${BUILD_TIMESTAMP}"
                echo "Build Number: ${env.BUILD_NUMBER}"
                echo "Deploy Server: ${params.DEPLOY_SERVER}"
                
                // Print Java version
                sh '''
                    echo "Java Version:"
                    java -version
                '''
                
                // Print Gradle version
                sh './gradlew --version'
            }
        }
        
        stage('Checkout') {
            steps {
                echo '========== Checkout Source Code =========='
                checkout scm
                
                // Display current commit info
                sh '''
                    echo "Current Git Commit:"
                    git log -1 --pretty=format:"%h - %an, %ar : %s"
                '''
            }
        }
        
        stage('Build') {
            steps {
                echo '========== Building Application =========='
                
                // Clean and build
                sh '''
                    ./gradlew clean build -x test \
                        -Pversion=${APP_VERSION} \
                        --no-daemon \
                        --console=plain
                '''
                
                echo 'Build completed successfully'
            }
        }
        
        stage('Unit Tests') {
            when {
                expression { params.RUN_TESTS == true }
            }
            steps {
                echo '========== Running Unit Tests =========='
                
                sh '''
                    ./gradlew test \
                        -PtestProfile=test \
                        --no-daemon \
                        --console=plain
                '''
            }
            post {
                always {
                    // Publish test results
                    junit '**/build/test-results/test/*.xml'
                    
                    // Publish test coverage report
                    jacoco(
                        execPattern: '**/build/jacoco/*.exec',
                        classPattern: '**/build/classes',
                        sourcePattern: '**/src/main/java'
                    )
                }
            }
        }
        
        stage('Code Quality Analysis') {
            when {
                expression { params.SKIP_SONAR == false }
            }
            steps {
                echo '========== Running Code Quality Analysis =========='
                
                // This is a placeholder for SonarQube analysis
                // In a real scenario, you would use the SonarQube Scanner
                script {
                    echo "Running SonarQube analysis..."
                    echo "SonarQube Token: [PROTECTED]"
                    
                    // Example SonarQube scan command (commented out)
                    // sh """
                    //     ./gradlew sonarqube \
                    //         -Dsonar.host.url=https://sonarqube.example.com \
                    //         -Dsonar.login=${SONAR_TOKEN} \
                    //         -Dsonar.projectKey=${APP_NAME} \
                    //         -Dsonar.projectName=${APP_NAME} \
                    //         -Dsonar.projectVersion=${APP_VERSION}
                    // """
                }
            }
        }
        
        stage('Package') {
            steps {
                echo '========== Packaging Application =========='
                
                sh '''
                    ./gradlew bootJar \
                        -Pversion=${APP_VERSION} \
                        --no-daemon \
                        --console=plain
                '''
                
                // Archive artifacts
                archiveArtifacts artifacts: '**/build/libs/*.jar', 
                                fingerprint: true,
                                allowEmptyArchive: false
            }
        }
        
        stage('Security Scan') {
            steps {
                echo '========== Running Security Scan =========='
                
                // Placeholder for security scanning
                script {
                    echo "Performing security scan..."
                    echo "API Key: [PROTECTED]"
                    
                    // Example: OWASP Dependency Check or similar
                    // sh './gradlew dependencyCheckAnalyze'
                }
            }
        }
        
        stage('Docker Build') {
            when {
                expression { 
                    params.BUILD_ENVIRONMENT == 'staging' || 
                    params.BUILD_ENVIRONMENT == 'production' 
                }
            }
            steps {
                echo '========== Building Docker Image =========='
                
                script {
                    echo "Building Docker image for ${APP_ENVIRONMENT} environment"
                    echo "Image tag: ${APP_NAME}:${APP_VERSION}-${BUILD_TIMESTAMP}"
                    
                    // Example Docker build command (commented out)
                    // sh """
                    //     docker build -t ${APP_NAME}:${APP_VERSION}-${BUILD_TIMESTAMP} .
                    //     docker tag ${APP_NAME}:${APP_VERSION}-${BUILD_TIMESTAMP} ${APP_NAME}:latest
                    // """
                }
            }
        }
        
        stage('Deploy to Environment') {
            when {
                expression { params.BUILD_ENVIRONMENT != 'development' }
            }
            steps {
                echo '========== Deploying Application =========='
                
                script {
                    echo "Deploying to ${APP_ENVIRONMENT} environment"
                    echo "Target server: ${params.DEPLOY_SERVER}"
                    echo "Using SSH Key: [PROTECTED]"
                    echo "Database Password: [PROTECTED]"
                    
                    // Example deployment commands (commented out)
                    // sh """
                    //     scp -i ${DEPLOY_SSH_KEY} \
                    //         build/libs/demo-${APP_VERSION}.jar \
                    //         deploy@${params.DEPLOY_SERVER}:/opt/apps/
                    //     
                    //     ssh -i ${DEPLOY_SSH_KEY} \
                    //         deploy@${params.DEPLOY_SERVER} \
                    //         'systemctl restart demo-app'
                    // """
                }
            }
        }
        
        stage('Smoke Tests') {
            when {
                expression { params.BUILD_ENVIRONMENT != 'development' }
            }
            steps {
                echo '========== Running Smoke Tests =========='
                
                script {
                    echo "Running smoke tests against ${params.DEPLOY_SERVER}"
                    
                    // Example smoke test
                    // sh """
                    //     curl -f http://${params.DEPLOY_SERVER}:8080/actuator/health || exit 1
                    // """
                }
            }
        }
    }
    
    post {
        always {
            echo '========== Pipeline Completed =========='
            echo "Final Status: ${currentBuild.currentResult}"
            
            // Clean workspace
            cleanWs()
        }
        success {
            echo '========== Build Successful =========='
            
            // Send success notification
            script {
                echo "Build ${env.BUILD_NUMBER} completed successfully for ${APP_ENVIRONMENT}"
                
                // Example: Send email or Slack notification
                // emailext (
                //     subject: "SUCCESS: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                //     body: "Build successful for ${APP_ENVIRONMENT} environment",
                //     to: 'team@example.com'
                // )
            }
        }
        failure {
            echo '========== Build Failed =========='
            
            // Send failure notification
            script {
                echo "Build ${env.BUILD_NUMBER} failed for ${APP_ENVIRONMENT}"
                
                // Example: Send email or Slack notification
                // emailext (
                //     subject: "FAILURE: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                //     body: "Build failed for ${APP_ENVIRONMENT} environment",
                //     to: 'team@example.com'
                // )
            }
        }
        unstable {
            echo '========== Build Unstable =========='
        }
    }
}
