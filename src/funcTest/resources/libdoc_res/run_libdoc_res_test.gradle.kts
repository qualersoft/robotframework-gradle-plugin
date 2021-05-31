plugins {
  id("de.qualersoft.robotframework")
  java
}

version = "0.0.1"

repositories {
  mavenLocal()
  mavenCentral()
}

tasks.register<de.qualersoft.robotframework.gradleplugin.tasks.LibdocTask>("libdocRun") {
  libdoc {
    outputDirectory.set(buildDir.resolve("doc/libdoc"))
    libraryOrResourceFile = "src/test/robots/keywords"
    format.set("html")
  }
}
