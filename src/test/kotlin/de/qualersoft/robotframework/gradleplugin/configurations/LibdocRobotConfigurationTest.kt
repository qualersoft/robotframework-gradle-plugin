package de.qualersoft.robotframework.gradleplugin.configurations

import de.qualersoft.robotframework.gradleplugin.PLUGIN_ID
import de.qualersoft.robotframework.gradleplugin.extensions.RobotFrameworkExtension
import de.qualersoft.robotframework.gradleplugin.robotframework
import io.kotest.assertions.show.show
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.collections.contain
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import java.io.File

class LibdocRobotConfigurationTest {

  private val project: Project = ProjectBuilder.builder()
    .withProjectDir(File("./")).build().also {
      it.pluginManager.apply(PLUGIN_ID)
    }
  private val rf: RobotFrameworkExtension = project.robotframework()

  @Test
  fun `generating default run arguments`() {
    val result = applyConfig { }.generateRunArguments()
    result.size shouldBe 0
  }

  @Test
  fun `generate with single lib or resource file`() {
    val result = applyConfig {
      it.libraryOrResourceFile = "./src/test/resources/ALibraryFile.robot"
    }.generateRunArguments()

    result.size shouldBe 1
    val lst = listOf(*result.first())
    lst should contain("libdoc")
    lst should haveElementContains("ALibraryFile.robot")
  }

  @Test
  fun `generate with wildcard lib or resource file`() {
    val result = applyConfig {
      it.libraryOrResourceFile = "src/test/resources/*.robot"
    }.generateRunArguments()

    result.size shouldBe 1
    val lst = listOf(*result.first())
    lst should haveElementContains("ALibraryFile.robot")
  }

  @Test
  fun `generate with folder wildcard lib or resource file`() {
    val result = applyConfig {
      it.libraryOrResourceFile = "**/resources/*.robot"
    }.generateRunArguments()

    result.size shouldBe 1
    val lst = listOf(*result.first())
    lst should haveElementContains("ALibraryFile.robot")
  }

  private fun applyConfig(conf: (LibdocRobotConfiguration) -> Unit): LibdocRobotConfiguration {
    rf.libdoc(conf)
    return rf.libdoc
  }

  private fun <C : Collection<String>> haveElementContains(expected: String) = object : Matcher<C> {
    override fun test(value: C) = MatcherResult(
      value.any { it.contains(expected) },
      { "Collection should have element which contains ${expected.show().value}; listing some elements ${value.take(5)}" },
      { "Collection should not have element which contains ${expected.show().value}" }
    )
  }
}