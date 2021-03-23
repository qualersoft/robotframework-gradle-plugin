pluginManagement {
  val kotlinVersion = "1.4.30"
  repositories {
    gradlePluginPortal()
    jcenter()
    mavenLocal()
    mavenCentral()
  }
  plugins {
    // realization
    kotlin("jvm") version kotlinVersion

    // quality
    id("org.sonarqube") version "3.0"
    id("io.gitlab.arturbosch.detekt") version "1.16.0"

    // documentation
    id("org.jetbrains.dokka") version "1.4.10.2"
  }
}

rootProject.name = "robotframework-gradle-plugin"