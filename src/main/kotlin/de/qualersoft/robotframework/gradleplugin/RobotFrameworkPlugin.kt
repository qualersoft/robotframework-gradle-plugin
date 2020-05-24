package de.qualersoft.robotframework.gradleplugin

import de.qualersoft.robotframework.gradleplugin.extensions.RobotFrameworkExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

private const val EXTENSION_NAME = "robotframework"

class RobotFrameworkPlugin : Plugin<Project> {

  override fun apply(target: Project) {
    val extension = target.extensions.create(
      EXTENSION_NAME,
      RobotFrameworkExtension::class.java,
      target)

    if (!target.pluginManager.hasPlugin("java")) {
      println("Applying java plugin")
      target.pluginManager.apply(JavaPlugin::class.java)
    }

    val robConf = target.configurations.register("robot")
    target.configurations.findByName(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME)?.also {
      it.extendsFrom(robConf.get())
    }

    target.afterEvaluate {
      it.configurations.findByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)?.also { rtConf ->
        extension.robotVersion.applyTo(rtConf)
      }
    }
  }
}

internal fun Project.robotframework(): RobotFrameworkExtension =
  extensions.getByName(EXTENSION_NAME) as? RobotFrameworkExtension
    ?: throw IllegalStateException("$EXTENSION_NAME is not of the correct type")
