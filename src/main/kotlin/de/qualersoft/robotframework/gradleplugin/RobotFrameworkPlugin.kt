package de.qualersoft.robotframework.gradleplugin

import de.qualersoft.robotframework.gradleplugin.extensions.RobotFrameworkExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

private const val EXTENSION_NAME = "robotframework"

class RobotFrameworkPlugin : Plugin<Project> {

  override fun apply(target: Project) {
    target.extensions.create(
      EXTENSION_NAME,
      RobotFrameworkExtension::class.java,
      target)

    if (!target.pluginManager.hasPlugin("java")) {
      println("Applying java plugin")
      target.pluginManager.apply(JavaPlugin::class.java)
    }

    val robConf = target.configurations.register("robot")
    val conf = target.configurations.findByName(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME)
    if (null != conf) {
      conf.extendsFrom(robConf.get())
    } else {
      println("No configuration found for 'implementation', not even after the 'java' plugin has been applied!")
    }
  }
}

internal fun Project.robotframework(): RobotFrameworkExtension =
  extensions.getByName(EXTENSION_NAME) as? RobotFrameworkExtension
    ?: throw IllegalStateException("$EXTENSION_NAME is not of the correct type")
