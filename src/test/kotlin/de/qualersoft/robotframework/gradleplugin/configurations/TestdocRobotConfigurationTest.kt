package de.qualersoft.robotframework.gradleplugin.configurations

import de.qualersoft.robotframework.gradleplugin.PLUGIN_ID
import de.qualersoft.robotframework.gradleplugin.extensions.RobotFrameworkExtension
import de.qualersoft.robotframework.gradleplugin.robotframework
import io.kotest.matchers.collections.shouldNotContainAnyOf
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

class TestdocRobotConfigurationTest : ConfigurationTestBase() {

  @Test
  fun `Generate default run arguments`() {
    val result = applyConfig {  }.generateArguments()
    result shouldNotContainAnyOf listOf(
      "--name", "--doc", "--metadata", "--settag",
      "--test", "--suite", "--include", "--exclude",
      "--argumentfiles", "--title"
    )
  }

  private val project: Project = ProjectBuilder.builder().build().also {
    it.pluginManager.apply(PLUGIN_ID)
  }
  private val rfExtension: RobotFrameworkExtension = project.robotframework()
  private fun applyConfig(conf: (TestdocRobotConfiguration) -> Unit): TestdocRobotConfiguration {
    rfExtension.testdoc(conf)
    return rfExtension.testdoc.get()
  }
}