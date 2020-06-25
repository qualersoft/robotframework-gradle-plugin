plugins {
  id("de.qualersoft.robotframework")
  `java`
}

repositories {
  mavenLocal()
  mavenCentral()
}

tasks.register<de.qualersoft.robotframework.gradleplugin.tasks.LibdocTask>("libdocRun") {
  libdoc {
    outputFile = file("libdoc.html")
    libraryOrResourceFile = "ALib"
    additionalPythonPaths.from(files(project.tasks.withType<Jar>().first().outputs.files,
        project.configurations.getByName("runtimeClasspath").files))
  }
}

dependencies {
  implementation(group = "org.robotframework", name = "javalib-core", version = "2.0.3")
}