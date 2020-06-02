plugins {
  id("de.qualersoft.robotframework")
}

repositories {
  mavenLocal()
  mavenCentral()
}

tasks.register<de.qualersoft.robotframework.gradleplugin.tasks.RobotTask>("robotRun") {
  sources = fileTree("src/test") {
    include("**/*.robot")
  }
}