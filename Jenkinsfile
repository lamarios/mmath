pipeline {
  agent any
  stages {
    stage('build') {
      agent any
      steps {
        sh 'mvn clean install'
      }
    }
  }
}