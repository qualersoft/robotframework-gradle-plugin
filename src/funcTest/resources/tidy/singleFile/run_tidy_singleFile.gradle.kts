plugins {
  id("de.qualersoft.robotframework")
}

version = "0.0.1"

repositories {
  mavenLocal()
  mavenCentral()
}

tasks.register<de.qualersoft.robotframework.gradleplugin.tasks.TidyTask>("tidyRun") {
  sources = files("src/test/robot/single2Tidy.robot")
  outputFile.set(file("cleaned.robot"))
}
