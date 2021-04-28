package de.qualersoft.robotframework.gradleplugin.configurations

import groovy.lang.Closure
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.be
import io.kotest.matchers.should
import io.kotest.matchers.shouldHave
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ExternalDependency
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

const val DEFAULT_GROUP = "org.robotframework"
const val DEFAULT_NAME = "robotframework"
const val DEFAULT_VERSION = "4.0.1"

internal class RobotframeworkConfigurationTest {

  private val project: Project = ProjectBuilder.builder().build().also {
    it.pluginManager.apply(JavaPlugin::class.java)
    it.repositories.mavenCentral()
  }
  private val sut: RobotframeworkConfiguration = RobotframeworkConfiguration(project)

  @Test
  fun createDependencyWithDefaults() {
    sut.applyTo(getRuntimeConfig())
    project shouldHave runtimeDependency(DEFAULT_GROUP, DEFAULT_NAME, DEFAULT_VERSION)
  }

  @Test
  fun createDefaultDependencyWithExtension() {
    sut.ext = "myExtension"
    sut.applyTo(getRuntimeConfig())
    project shouldHave runtimeDependency(DEFAULT_GROUP, DEFAULT_NAME, DEFAULT_VERSION, ext = "myExtension")
  }

  @Test
  fun createDefaultDependencyWithClassifier() {
    sut.classifier = "myClassifier"
    sut.applyTo(getRuntimeConfig())
    project shouldHave runtimeDependency(DEFAULT_GROUP, DEFAULT_NAME, DEFAULT_VERSION, classifier = "myClassifier")
  }

  @Test
  fun createDefaultDependencyWithAction() {
    var wasCalled = false
    sut.configureClosure = object : Closure<Any>(this) {
      override fun call() {
        wasCalled = true
      }
    }
    sut.applyTo(getRuntimeConfig())
    wasCalled should be(true)
  }

  @Test
  fun createDependencyWithEachFieldChanged() {
    sut.group = "myGroup"
    sut.name = "myName"
    sut.version = "0.0.0-myVersion"
    sut.ext = "myExt"
    sut.classifier = "myClassifier"
    sut.applyTo(getRuntimeConfig())

    project shouldHave runtimeDependency("myGroup", "myName", "0.0.0-myVersion", "myClassifier", "myExt")

    // after apply nothing should have changed (just to get the last percentages of getter-coverage)
    sut.group should be("myGroup")
    sut.name should be("myName")
    sut.version should be("0.0.0-myVersion")
    sut.ext!! should be("myExt")
    sut.classifier!! should be("myClassifier")
  }

  private fun getRuntimeConfig(): Configuration = project.configurations
    .findByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)!!

  private fun runtimeDependency(
    group: String,
    name: String,
    version: String,
    classifier: String? = null,
    ext: String? = null
  ) = object : Matcher<Project> {
    override fun test(value: Project): MatcherResult {
      val rtConf = value.configurations.findByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)!!
      val extDeps = rtConf.dependencies.withType(ExternalDependency::class.java)
      val found = extDeps.find {
        it.group == group &&
          it.name == name &&
          it.version == version &&
          hasArtifact(it, name, classifier, ext)
      }
      return MatcherResult(
        null != found,
        "Project should contain RobotFramework runtime dependency",
        "Project should not contain RobotFramework runtime dependency"
      )
    }
  }

  private fun hasArtifact(dependency: ExternalDependency, name: String, classifier: String?, ext: String?): Boolean {
    return if (null != classifier || null != ext) {
      // only if one is set, an artifact entry will be generated
      null != dependency.artifacts.find {
        it.name == name && if (null != ext) {
          it.extension == ext
        } else { // if no extension was set it defaults to jar
          it.extension == "jar"
        } && it.classifier == classifier
      }
    } else {
      true // we return true because if no classifier and no extension are set -> no artifact will be created
    }
  }
}