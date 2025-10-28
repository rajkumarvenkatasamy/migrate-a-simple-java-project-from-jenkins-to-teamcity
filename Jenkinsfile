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
    }
    
    // Environment variables
    environment {
        // Non-sensitive environment variables
        APP_NAME = 'demo-spring-boot-app'
        GRADLE_OPTS = '-Dorg.gradle.daemon=false'
        
        // Derived environment variable from parameter
        APP_ENVIRONMENT = "${params.BUILD_ENVIRONMENT}"
        APP_VERSION = "${params.BUILD_VERSION}"
        
        // Sensitive environment variables (using credentials)
        // Note: Create these credentials in Jenkins before running
        // Or comment out if not yet configured
        DB_PASSWORD = credentials('database-password')
        // API_KEY = credentials('api-key')
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
                echo "Build Number: ${env.BUILD_NUMBER}"
                
                // Print Java version
                sh '''
                    echo "Java Version:"
                    java -version
                '''
                
                // Make gradlew executable (needed on Linux/Unix)
                sh 'chmod +x gradlew'
                
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
                
                sh 'chmod +x gradlew'

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
                
                sh 'chmod +x gradlew'

                sh '''
                    ./gradlew test \
                        -PtestProfile=test \
                        --no-daemon \
                        --console=plain
                '''
            }
        }
                         
    }
    
    post {
        always {
            script {
                echo '========== Pipeline Completed =========='
                echo "Final Status: ${currentBuild.currentResult}"
                
                // Clean workspace only if running on an agent
                try {
                    cleanWs()
                } catch (Exception e) {
                    echo "Unable to clean workspace: ${e.message}"
                }
            }
        }
        success {
            script {
                echo '========== Build Successful =========='
                echo "Build ${env.BUILD_NUMBER} completed successfully"
                
                // Example: Send email or Slack notification
                // emailext (
                //     subject: "SUCCESS: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                //     body: "Build successful for ${params.BUILD_ENVIRONMENT} environment",
                //     to: 'team@example.com'
                // )
            }
        }
        failure {
            script {
                echo '========== Build Failed =========='
                echo "Build ${env.BUILD_NUMBER} failed"
                
                // Example: Send email or Slack notification
                // emailext (
                //     subject: "FAILURE: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                //     body: "Build failed for ${params.BUILD_ENVIRONMENT} environment",
                //     to: 'team@example.com'
                // )
            }
        }
        unstable {
            script {
                echo '========== Build Unstable =========='
            }
        }
    }
}
