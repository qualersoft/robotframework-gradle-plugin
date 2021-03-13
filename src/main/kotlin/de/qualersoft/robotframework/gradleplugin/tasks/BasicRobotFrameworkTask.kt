package de.qualersoft.robotframework.gradleplugin.tasks

import de.qualersoft.robotframework.gradleplugin.ROBOT_CONFIGURATION_NAME
import de.qualersoft.robotframework.gradleplugin.RobotFrameworkPlugin
import de.qualersoft.robotframework.gradleplugin.robotframework
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.bundling.Jar
import java.io.File

abstract class BasicRobotFrameworkTask : JavaExec() {

  init {
    val rfVersion = project.robotframework().robotVersion
    mainClass.set(rfVersion.get().mainClass)
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
    val robotArgs = listOf(cmd) + rfArgs + (additionalArgs ?: listOf())
    println("Starting cmd '$cmd' with arguments $rfArgs and task specific additional arguments $additionalArgs")
    ensureLibraries()
    args(robotArgs)
    super.exec()
  }

  private fun ensureLibraries() {
    val jar = getRobotLib()
    println("Found robot-jar: ${jar?.absoluteFile}")
    classpath(project.files(jar))

    // adding packed jar
    val packJar = project.tasks.getByName("jar") as Jar
    classpath(packJar.archiveFile.get())

    // TODO Create switch to choose between jar or classes
//    val jpc = project.convention.getPlugin(JavaPluginConvention::class.java)
//    classpath(jpc.sourceSets.getByName("main").output.classesDirs)
//    classpath(jpc.sourceSets.getByName("main").output.resourcesDir)

    // adding dependencies
    classpath(project.configurations.getByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)
      .resolvedConfiguration.resolvedArtifacts.map { it.file }.toTypedArray())
  }

  private fun getRobotLib(): File? {
    val rfVersion = extension.robotVersion.get()
    val artifacts: Set<ResolvedArtifact>? = project.configurations
      .findByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)
      ?.resolvedConfiguration?.resolvedArtifacts
    return artifacts?.find {
      val id = it.moduleVersion.id
      // TODO make version-comparison more flexible e.g. allow >=
      id.version == rfVersion.version
          && id.group == rfVersion.group
          && id.name == rfVersion.name
    }?.file
  }
}