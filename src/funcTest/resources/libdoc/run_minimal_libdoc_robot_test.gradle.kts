plugins {
  id("de.qualersoft.robotframework")
}

repositories {
  mavenLocal()
  mavenCentral()
}

tasks.register<de.qualersoft.robotframework.gradleplugin.tasks.LibdocTask>("libdocRun") {
  libdoc {
    outputFile = file("libdoc.html")
    libraryOrResourceFile = file("src/test/robot/test.robot")
  }
}