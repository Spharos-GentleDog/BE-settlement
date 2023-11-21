pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh '''
                    chmod +x gradlew
                    ./gradlew build -x test
                '''
            }
        }
        stage('DockerSize') {
            steps {
                sh '''
                    docker stop settlement || true
                    docker rm settlement || true
                    docker rmi settlement || true
                    docker build -t settlement .
                    echo "settlement: build settlement"
                '''
            }
        }
        stage('Deploy') {
            steps {
                sh '''
                docker run -e EUREKA_URL="${EUREKA_URL}" -e MASTER_DB_URL="${MASTER_DB_URL}/settlement" -e MASTER_DB_USERNAME="${MASTER_DB_USERNAME}" -e MASTER_DB_PASSWORD="${MASTER_DB_PASSWORD}" -e BOOTSTRAP_SERVER="${BOOTSTRAP_SERVERS}" -d --name settlement --network gentledog settlement
                echo "settlement: run success"
                '''
                }
        }
    }
}