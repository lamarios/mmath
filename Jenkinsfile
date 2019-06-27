pipeline {
  agent any
  stages {
    stage('build') {
      agent {
        docker {
          image 'maven'
        }

      }
      steps {
        sh 'mvn clean install'
      }
    }
  }
}