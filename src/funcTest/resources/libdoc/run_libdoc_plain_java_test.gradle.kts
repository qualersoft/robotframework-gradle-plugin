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
    outputFile.set(file("libdoc.html"))
    libraryOrResourceFile = "src/main/java/ALib.java"
  }
}

dependencies {
  implementation(group = "org.robotframework", name = "javalib-core", version = "2.0.3")
}
