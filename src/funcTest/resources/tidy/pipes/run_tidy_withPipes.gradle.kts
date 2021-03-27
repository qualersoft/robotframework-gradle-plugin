plugins {
  id("de.qualersoft.robotframework")
}

version = "0.0.1"

repositories {
  mavenLocal()
  mavenCentral()
}

tasks.register<de.qualersoft.robotframework.gradleplugin.tasks.TidyTask>("tidyRun") {
  sources = files("src/test/robot/pipeTidy.robot")
  tidy {
    usepipes.set(true)
    inplace.set(true)
  }
}