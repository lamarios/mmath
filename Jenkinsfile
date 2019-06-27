pipeline {
  agent any
  stages {
    stage('build') {
      agent {
        docker {
          image 'gonzague/maven-nodejs'
        }

      }
      steps {
        sh 'mvn clean install'
      }
    }
  }
}