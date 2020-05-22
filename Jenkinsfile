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
      withCredentials([string(credentialsId: 'CODECOV_TOKEN', variable: 'CC_TOKEN')]) {
        powershell(script:  '''$AllProtocols = [System.Net.SecurityProtocolType]'Tls11,Tls12'
          [System.Net.ServicePointManager]::SecurityProtocol = $AllProtocols
          (Invoke-WebRequest -Uri https://codecov.io/bash -UseBasicParsing).Content | Out-File -File ccScript -Force -Encoding UTF8NoBOM''')
        sh(script: './ccScript -t $CC_TOKEN')
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
    sh "./gradlew $args"
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