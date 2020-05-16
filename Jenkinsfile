node {
  withCredentials([usernamePassword(credentialsId: 'NEXUS_DEPLOY', passwordVariable: 'ORG_GRADLE_PROJECT_mavenPwd', usernameVariable: 'ORG_GRADLE_PROJECT_mavenUsr')]) {
    // some block

    stage("Checkout") {
      checkout scm
    }
    stage("Static Analyse") {
      echo "analyzing code..."
      analyzeWithSonarQubeAndWaitForQualityGoal();
    }
    stage("Compiling") {
      echo "Compiling artifacts..."
      exec './gradlew clean classes'
      stash("build")
    }
    stage("Test") {
      echo "testing artifacts..."
      try {
        unstash("build")
        exec './gradlew test'
      } finally {
        junit '**/build/test-results/test/*.xml'
      }
    }
    stage("Package") {
      echo "Package artifacts..."
      unstash("build")
      exec './gradlew jar'
    }
    stage("Publish") {
      echo "publish artifacts..."
    }
  }
}

void analyzeWithSonarQubeAndWaitForQualityGoal() {
  withSonarQubeEnv('SQ_MeMathze') {
    exec "./gradlew sonarqube -D'sonar.login=${SONAR_AUTH_TOKEN}'"
  }
  timeout(time: 2, unit: 'MINUTES') {
    def qg = waitForQualityGate(webhookSecretId: 'sonar_build')
    if (qg.status != 'OK') {
      currentBuild.result = 'UNSTABLE'
    }
  }
}

def exec(cmd) {
  if (Boolean.valueOf(env.UNIX)) {
    sh cmd
  } else {
    bat cmd
  }
}