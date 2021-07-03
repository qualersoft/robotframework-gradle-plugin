pluginManagement {
  val kotlinVersion = "1.5.0"
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
  plugins {
    // realization
    kotlin("jvm") version kotlinVersion

    // quality
    id("org.unbroken-dome.test-sets") version "4.0.0"
    // workaround to integrate jacoco coverage into integration tests. (See https://github.com/gradle/gradle/issues/1465)
    id("pl.droidsonroids.jacoco.testkit") version "1.0.8"
    id("org.sonarqube") version "3.1.1"
    // Remark: If upgrade to 1.17 remove jcenter dependency from build-script
    id("io.gitlab.arturbosch.detekt") version "1.17.1"

    // documentation
    id("org.jetbrains.dokka") version "1.4.32"
    id("org.asciidoctor.jvm.convert") version "3.3.2"

    // publishing
    id("com.gradle.plugin-publish") version "0.15.0"
    id("com.github.ben-manes.versions") version "0.38.0"
  }
}

rootProject.name = "robotframework-gradle-plugin"
