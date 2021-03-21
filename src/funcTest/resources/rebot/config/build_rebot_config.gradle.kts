plugins {
  id("de.qualersoft.robotframework")
}

version = "0.0.1"

repositories {
  mavenLocal()
  mavenCentral()
}

tasks.register<de.qualersoft.robotframework.gradleplugin.tasks.RebotTask>("rebotRun") {
  sources = files("builds/test-results/robot/*.xml")
  rebot {
    outputDir.set(project.buildDir)
    xUnit = null
    log = File("reports/robot/rfLog.html")
    report = File("reports/robot/rfReport.html")
  }
}