pluginManagement {
  val kotlinVersion = "1.4.30"
  repositories {
    gradlePluginPortal()
    jcenter()
    mavenCentral()
  }
  plugins {
    // realization
    kotlin("jvm") version kotlinVersion

    // quality
    id("org.sonarqube") version "3.1.1"
    id("io.gitlab.arturbosch.detekt") version "1.16.0"

    // documentation
    id("org.jetbrains.dokka") version "1.4.30"
    id("org.asciidoctor.jvm.convert") version "3.3.2"

    // publishing
    id("com.gradle.plugin-publish") version "0.14.0"
  }
}

rootProject.name = "robotframework-gradle-plugin"