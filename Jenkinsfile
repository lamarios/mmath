def label = "maven-${UUID.randomUUID().toString()}"

podTemplate(label: label, containers: [
  containerTemplate(name: 'maven', image: 'gonzague/maven-nodejs', ttyEnabled: true, command: 'cat')
  ]) {

  node(label) {
    stage('Build') {
      container('maven') {
          sh 'mvn -B clean package'
      }
    }
  }
}
