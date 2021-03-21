plugins {
  id("de.qualersoft.robotframework")
}

version = "0.0.1"

repositories {
  mavenLocal()
  mavenCentral()
}

tasks.register<de.qualersoft.robotframework.gradleplugin.tasks.TestdocTask>("testdocRun") {
  sources = files("src/test/robot")
  testdoc {
    exclude = mutableListOf("DocTag")
  }
}