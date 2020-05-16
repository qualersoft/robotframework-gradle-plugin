pluginManagement {
  val kotlinVersion = "1.3.72"
  repositories {
    gradlePluginPortal()
    mavenLocal()
    mavenCentral()
  }
  plugins {
    kotlin("jvm") version kotlinVersion
    id("org.jetbrains.dokka") version "0.10.1"
    id("org.sonarqube") version "2.8"
    id("io.gitlab.arturbosch.detekt") version "1.9.0"
  }
}