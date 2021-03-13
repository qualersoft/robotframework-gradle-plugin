package de.qualersoft.robotframework.gradleplugin.configurations

import de.qualersoft.robotframework.gradleplugin.PLUGIN_ID
import de.qualersoft.robotframework.gradleplugin.extensions.RobotFrameworkExtension
import de.qualersoft.robotframework.gradleplugin.robotframework
import io.kotest.matchers.collections.contain
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.collections.shouldNotContainAnyOf
import io.kotest.matchers.should
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class TidyRobotConfigurationTest : ConfigurationTestBase() {

  @Test
  fun `Generate default run arguments`() {
    val result = applyConfigToArgList { }

    assertAll(
      { result shouldNotContainAnyOf listOf("--inplace", "--recursive", "--usepipes") },
      { result shouldContainInOrder listOf("--lineseparator", "native", "--spacecount", "4") }
    )
  }

  @Test
  fun `Generate with inplace`() {
    val result = applyConfigToArgList {
      it.inplace.set(true)
    }

    result should contain("--inplace")
  }

  @Test
  fun `Generate with recursive`() {
    val result = applyConfigToArgList {
      it.recursive.set(true)
    }

    result should contain("--recursive")
  }

  @Test
  fun `Generate with usepipes`() {
    val result = applyConfigToArgList {
      it.usepipes.set(true)
    }

    result should contain("--usepipes")
  }

  @Test
  fun `Generate with lineseparator`() {
    val result = applyConfigToArgList {
      it.lineseparator.set("windows")
    }
    result shouldContainInOrder listOf("--lineseparator", "windows")
  }

  @Test
  fun `Generate with lineseparator reset`() {
    applyConfig { it.lineseparator.set("some") }
    val result = applyConfigToArgList {
      it.lineseparator.set(null as String?)
    }
    result shouldContainInOrder listOf("--lineseparator", "native")
  }

  @Test
  fun `Generate with spacecount`() {
    val result = applyConfigToArgList {
      it.spacecount.set(2)
    }
    result shouldContainInOrder listOf("--spacecount", "2")
  }

  private val project: Project = ProjectBuilder.builder().build().also {
    it.pluginManager.apply(PLUGIN_ID)
  }
  private val rfExtension: RobotFrameworkExtension = project.robotframework()
  private fun applyConfigToArgList(conf: (TidyRobotConfiguration) -> Unit): List<String> =
    applyConfig(conf).generateArguments().toList()

  private fun applyConfig(conf: (TidyRobotConfiguration) -> Unit): TidyRobotConfiguration {
    rfExtension.tidy(conf)
    return rfExtension.tidy.get()
  }
}