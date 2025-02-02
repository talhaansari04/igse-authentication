pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                // Checkout the source code from a Git repository
                //git branch: 'main', url: 'https://github.com/your-repo/your-gradle-project.git'
                echo "Git branch checkout"
            }
        }

        stage('Build') {
            steps {
                // Run the Gradle build command
                //sh './gradlew build'
                echo "Build is running"
            }
        }

        stage('Test') {
            steps {
                // Run the Gradle test command
                //sh './gradlew test'
                echo "Test cases running"
            }
        }

    }

    post {
        success {
            // Actions to perform if the pipeline succeeds
            echo 'Pipeline succeeded!'
        }
        failure {
            // Actions to perform if the pipeline fails
            echo 'Pipeline failed!'
        }
    }
}