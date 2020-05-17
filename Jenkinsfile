node {
  withCredentials([usernamePassword(credentialsId: 'NEXUS_DEPLOY', passwordVariable: 'ORG_GRADLE_PROJECT_mavenPwd', usernameVariable: 'ORG_GRADLE_PROJECT_mavenUsr')]) {
    // some block

    stage("Checkout") {
      checkout scm
    }
    stage("Static Analyse") {
      echo "analyzing code..."
      execGradle 'detekt'
    }
    stage("Compiling") {
      echo "Compiling artifacts..."
      execGradle 'clean classes'
      stash("build")
    }
    stage("Test") {
      echo "testing artifacts..."
      try {
        unstash("build")
        execGradle 'test'
      } finally {
        junit '**/build/test-results/test/*.xml'
      }
    }
    stage("Report") {
      execGradle 'jacocoTestReport'
      withCredentials([string(credentialsId: 'CODECOV_TOKEN', variable: 'TOKEN')]) {
        def bsh = powershell(returnStdout: true, script:  '''$AllProtocols = [System.Net.SecurityProtocolType]'Tls11,Tls12'
          [System.Net.ServicePointManager]::SecurityProtocol = $AllProtocols
          (Invoke-WebRequest -Uri https://codecov.io/bash -UseBasicParsing).Content''')
        println "DEBUG: $bsh"
        bash bsh -t TOKEN
      }
      analyzeWithSonarQubeAndWaitForQualityGoal()
    }
    stage("Package") {
      echo "Package artifacts..."
      unstash("build")
      execGradle 'jar'
    }
    stage("Publish") {
      echo "publish artifacts..."
    }
  }
}

void analyzeWithSonarQubeAndWaitForQualityGoal() {
  withSonarQubeEnv('SQ_MeMathze') {
    execGradle "sonarqube -D'sonar.login=${SONAR_AUTH_TOKEN}'"
  }
  timeout(time: 2, unit: 'MINUTES') {
    def qg = waitForQualityGate(webhookSecretId: 'sonar_build')
    if (qg.status != 'OK') {
      currentBuild.result = 'UNSTABLE'
    }
  }
}

def execGradle(args) {
  if (Boolean.valueOf(env.UNIX)) {
    sh "./gradle $args"
  } else {
    bat "./gradlew $args"
  }
}

def exec(cmd) {
  if (Boolean.valueOf(env.UNIX)) {
    sh cmd
  } else {
    bat cmd
  }
}