pipeline {
  agent any
  stages {
    stage('build') {
      agent {
        docker {
          image 'gonzague/maven-nodejs'
          args '-v "/maven:/root/.m2"'
        }

      }
      steps {
        sh 'mvn clean install'
      }
    }
  }
}