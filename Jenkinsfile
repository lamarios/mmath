pipeline {
  agent any
  stages {
    stage('build') {
      agent {
        docker {
          args '-v "/maven:/root/.m2"'
          image 'gonzague/maven-nodejs:latest'
        }

      }
      steps {
        sh 'mvn clean install'
      }
    }
  }
}