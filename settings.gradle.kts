pluginManagement {
  val kotlinVersion = "1.4.30"
  repositories {
    gradlePluginPortal()
    jcenter()
    mavenLocal()
    mavenCentral()
  }
  plugins {
    kotlin("jvm") version kotlinVersion
    id("org.jetbrains.dokka") version "1.4.10.2"
    id("org.sonarqube") version "3.0"
    id("io.gitlab.arturbosch.detekt") version "1.15.0"
  }
}