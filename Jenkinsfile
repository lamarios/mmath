pipeline {
  agent any
  stages {
    stage('build') {
      agent {
        docker {
          image 'gonzague/maven-nodejs:latest'
        }

      }
      steps {
        sh 'mvn clean install'
      }
    }
  }
}