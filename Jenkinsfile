pipeline {
  agent any
  stages {
    stage('build') {
      agent {
        docker {
          image 'gonzague/maven-nodejs:latest'
          args '-v /build-cache/maven:/root/.m2'
        }

      }
      steps {
        sh 'mvn clean install'
      }
    }
  }
}