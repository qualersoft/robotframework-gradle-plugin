node {
  try {
    withCredentials([usernamePassword(credentialsId: 'NEXUS_DEPLOY', passwordVariable: 'ORG_GRADLE_PROJECT_mavenPwd', usernameVariable: 'ORG_GRADLE_PROJECT_mavenUsr')]) {
      stage("Checkout") {
        checkout scm
      }
      stage("Static Analyse") {
        echo "analyzing code..."
        execGradle 'detekt'

        echo "analyzing dependencies"
        withEnv(["PATH+FOSSA=${env.ALLUSERSPROFILE}\\fossa-cli"]) {
          powershell(script: """\$AllProtocols = [System.Net.SecurityProtocolType]'Tls11,Tls12'
              [System.Net.ServicePointManager]::SecurityProtocol = \$AllProtocols
              (Invoke-WebRequest -Headers @{'Cache-Control' = 'no-cache'} -Uri 'https://raw.githubusercontent.com/fossas/fossa-cli/master/install.ps1' -UseBasicParsing).Content | Out-File -File fossaInstall.ps1 -Force -Encoding UTF8NoBOM""")
          powershell(script: './fossaInstall.ps1')
          // after installing fossa we can delete script immediately
          powershell(script: 'rd ./fossaInstall.ps1')
          exec 'fossa init'
          withCredentials([string(credentialsId: 'FOSSA_TOKEN', variable: 'FOSSA_API_KEY')]) {
            exec 'fossa analyze'
          }
        }
      }
      stage("Compiling") {
        echo "Compiling artifacts..."
        execGradle 'clean classes'
        stash("build")
      }
      stage("Testing") {
        parallel([
          "fossaTest" : {
            withEnv(["PATH+FOSSA=${env.ALLUSERSPROFILE}\\fossa-cli"]) {
              withCredentials([string(credentialsId: 'FOSSA_TOKEN', variable: 'FOSSA_API_KEY')]) {
                exec 'fossa test'
              }
            }
          },
          "unitTests" : {
            stage("Unit tests") {
              echo "testing artifacts..."
              try {
                unstash("build")
                execGradle 'test'
  //               stash("test")
              } finally {
                junit '**/build/test-results/test/*.xml'
              }
            }
          },
          "functionalTests" : {
            stage("Functional tests") {
              echo "executing functional tests"
              try {
                unstash("build")
                execGradle 'funcTest'
  //               stash("funcTest")
              } finally {
                junit '**/build/test-results/funcTest/*.xml'
              }
            }
          }
        ])
      }
      stage("Report") {
  //       unstash("test")
  //       unstash("funcTest")
        execGradle 'jacocoMerge'
        execGradle 'reportMerge'
        withCredentials([string(credentialsId: 'CODECOV_TOKEN', variable: 'CC_TOKEN')]) {
          powershell(script: '''(New-Object System.Net.WebClient).DownloadFile("https://github.com/codecov/codecov-exe/releases/download/1.12.0/codecov-win7-x64.zip", (Join-Path $pwd "Codecov.zip"))
          Expand-Archive -Force .\\Codecov.zip -DestinationPath .\\Codecov''')
          powershell(script: '.\\Codecov\\codecov.exe -f "build\\reports\\jacoco\\**\\*.xml" -t $ENV:CC_TOKEN')
          powershell(script: '''rd -Force -Recurse .\\Codecov\\
          rd -Force .\\Codecov.zip''')
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
  } finally {
    archiveArtifacts artifacts: ['build/reports/jacoco/reportMerge/**', 'build/reports/detekt/*.*'], allowEmptyArchive: true
  }
}

void analyzeWithSonarQubeAndWaitForQualityGoal() {
  withSonarQubeEnv('SQ_MeMathze') {
    execGradle "sonarqube -D'sonar.login=${SONAR_AUTH_TOKEN}' -D'sonar.branch.name=${env.BRANCH_NAME}'"
  }
  timeout(time: 2, unit: 'MINUTES') {
    def qg = waitForQualityGate(webhookSecretId: 'sonar_build')
    if (qg.status != 'OK') {
      currentBuild.result = 'UNSTABLE'
    }
  }
}

def execGradle(args) {
  exec "./gradlew $args"
}

def exec(cmd) {
  if (Boolean.valueOf(env.UNIX)) {
    sh cmd
  } else {
    bat cmd
  }
}