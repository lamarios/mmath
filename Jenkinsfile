pipeline {
  agent any
  stages {
    stage('build') {
      agent {
        docker {
          image 'gonzague/maven-nodejs:latest'
          args '-v "/maven:/root/.m2" --pull'
        }

      }
      steps {
        sh 'mvn clean install'
      }
    }
  }
}