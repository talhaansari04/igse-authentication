pipeline {

    stages {
        stage('Checkout') {
            steps {
                // Checkout the source code from a Git repository
                git branch: 'main', url: 'https://github.com/your-repo/your-gradle-project.git'
            }
        }

        stage('Build') {
            steps {
                // Run the Gradle build command
                sh './gradlew build'
            }
        }

        stage('Test') {
            steps {
                // Run the Gradle test command
                sh './gradlew test'
            }
        }

        stage('Archive Artifacts') {
            steps {
                // Archive the build artifacts (e.g., JAR, WAR files)
                archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
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