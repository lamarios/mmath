pipeline {
  agent any
  stages {
    stage('build') {
      agent {
        docker {
          image 'maven:3.6.1-jdk-11-slim'
        }

      }
      steps {
        sh 'mvn clean install'
      }
    }
  }
}