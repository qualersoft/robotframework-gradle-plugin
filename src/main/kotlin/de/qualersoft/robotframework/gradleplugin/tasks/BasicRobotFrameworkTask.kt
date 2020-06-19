package de.qualersoft.robotframework.gradleplugin.tasks

import de.qualersoft.robotframework.gradleplugin.robotframework
import org.gradle.api.DefaultTask
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.JavaExec
import org.robotframework.RobotFramework
import java.io.File

abstract class BasicRobotFrameworkTask : JavaExec() {

  init {
    val rfVersion = project.robotframework().robotVersion
    mainClass.set(rfVersion.mainClass)
  }
  /**
   * Additional properties that will be append to end of the configuration arguments.
   * Can be used to 'override' configuration or to provide task specific parameters.
   */
  @Input
  var rfArgs = listOf<String>()

  /**
   * Provide access to the `RobotFrameworkExtension`
   */
  @Internal
  protected val extension = project.robotframework()

  internal fun executeRobotCommand(cmd: String, additionalArgs: Collection<String>? = null) {
    val robotArgs = listOf(cmd)  + rfArgs + (additionalArgs ?: listOf())
    println("Starting cmd '$cmd' with arguments $rfArgs and task specific additional arguments $additionalArgs and ")
    ensureRobotLib()
    args(robotArgs)
    super.exec()
  }

  private fun ensureRobotLib() {
    val jar = getRobotLib()
    classpath(project.files(jar))
  }

  protected fun getRobotLib(): File? {
    val rfVersion = extension.robotVersion
    return project.configurations.findByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)?.find {
      it.isFile && it.name.contains(rfVersion.name)
    }
  }
}