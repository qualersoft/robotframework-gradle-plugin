package de.qualersoft.robotframework.gradleplugin

import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.shouldNot
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

internal const val PLUGIN_ID = "de.qualersoft.robotframework"

class RobotFrameworkPluginTest {

  private val project = ProjectBuilder.builder().build()

  @Test
  fun `Apply the plugin by ID should use the plugin`() {
    project.pluginManager.apply(PLUGIN_ID)

    val actual: RobotFrameworkPlugin? = project.plugins.getPlugin(RobotFrameworkPlugin::class.java)
    actual shouldNot beNull()
  }

  @Test
  fun `Apply the plugin should register the 'robotframework' extension`() {
    project.pluginManager.apply(RobotFrameworkPlugin::class.java)

    project.robotframework() shouldNot beNull()
  }

  @Test
  fun `Apply the plugin with java plugin also work`() {
    project.pluginManager.apply("java")
    project.pluginManager.apply(PLUGIN_ID)

    val actual: RobotFrameworkPlugin? = project.plugins.getPlugin(RobotFrameworkPlugin::class.java)
    actual shouldNot beNull()
  }

  @Test
  fun `Apply the the plugin without java plugin should add it`() {
    project.pluginManager.apply(PLUGIN_ID)

    val actual = project.plugins.getPlugin(JavaPlugin::class.java)
    actual shouldNot beNull()
  }
}
