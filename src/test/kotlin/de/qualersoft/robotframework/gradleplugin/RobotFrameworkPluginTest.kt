package de.qualersoft.robotframework.gradleplugin

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder

internal const val PLUGIN_ID = "de.qualersoft.robotframework"

class RobotFrameworkPluginTest : StringSpec ({
  "Using the Plugin" should {
    val project = ProjectBuilder.builder().build()
    "Apply the Plugin ID" {
      project.pluginManager.apply(PLUGIN_ID)

      val actual: RobotFrameworkPlugin? = project.plugins.getPlugin(RobotFrameworkPlugin::class.java)
      actual shouldNot beNull()
    }

    "Register the 'robotframework' extension" {
      project.pluginManager.apply(RobotFrameworkPlugin::class.java)

      project.robotframework() shouldNot beNull()
    }

    "with java plugin also work" {
      project.pluginManager.apply("java")
      project.pluginManager.apply(PLUGIN_ID)

      val actual: RobotFrameworkPlugin? = project.plugins.getPlugin(RobotFrameworkPlugin::class.java)
      actual shouldNot beNull()
    }

    "without java plugin should add it" {
      project.pluginManager.apply(PLUGIN_ID)

      val actual = project.plugins.getPlugin(JavaPlugin::class.java)
      actual shouldNot beNull()
    }
  }
})
