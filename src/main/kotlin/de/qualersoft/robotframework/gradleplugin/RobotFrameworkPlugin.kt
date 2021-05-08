package de.qualersoft.robotframework.gradleplugin

import de.qualersoft.robotframework.gradleplugin.extensions.RobotFrameworkExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

private const val EXTENSION_NAME = "robotframework"
const val ROBOT_CONFIGURATION_NAME = "robot"

class RobotFrameworkPlugin : Plugin<Project> {

  override fun apply(target: Project) {
    val extension = target.extensions.create(
      EXTENSION_NAME,
      RobotFrameworkExtension::class.java,
      target
    )

    applyJavaPluginIfRequired(target)
    registerRobotConfiguration(target)

    target.afterEvaluate {
      val robConf = it.configurations.findByName(ROBOT_CONFIGURATION_NAME)
      it.configurations.findByName(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME)?.also { cnf ->
        cnf.extendsFrom(robConf)
      }

      it.configurations.findByName(JavaPlugin.RUNTIME_ONLY_CONFIGURATION_NAME)?.also { rtConf ->
        extension.robotVersion.get().applyTo(rtConf)
      }
    }
  }

  private fun applyJavaPluginIfRequired(project: Project) {
    if (null == project.configurations.findByName(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME)) {
      println("Applying java plugin")
      project.pluginManager.apply(JavaPlugin::class.java)
    }
  }

  private fun registerRobotConfiguration(project: Project) {
    project.configurations.create(ROBOT_CONFIGURATION_NAME)
  }
}

internal fun Project.robotframework(): RobotFrameworkExtension =
  extensions.getByName(EXTENSION_NAME) as? RobotFrameworkExtension
    ?: throw IllegalStateException("$EXTENSION_NAME is not of the correct type")
