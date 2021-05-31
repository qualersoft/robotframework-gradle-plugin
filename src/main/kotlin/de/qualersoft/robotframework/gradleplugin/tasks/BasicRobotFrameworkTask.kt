package de.qualersoft.robotframework.gradleplugin.tasks

import de.qualersoft.robotframework.gradleplugin.extensions.RobotFrameworkExtension
import de.qualersoft.robotframework.gradleplugin.robotframework
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.bundling.Jar
import java.io.File

abstract class BasicRobotFrameworkTask : JavaExec() {

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
  protected val rfExtension: RobotFrameworkExtension = project.robotframework()

  init {
    val rfVersion = rfExtension.robotVersion
    mainClass.set(rfVersion.get().mainClass)
  }

  internal fun executeRobotCommand(cmd: String, additionalArgs: Collection<String>? = null) {
    val robotArgs = listOf(cmd) + rfArgs + (additionalArgs ?: listOf())
    println("Starting cmd '$cmd' with arguments $rfArgs and task specific additional arguments $additionalArgs")
    ensureLibraries()
    args = robotArgs
    println("DEBUG: execArgs: $args")
    super.exec()
  }

  private fun ensureLibraries() {
    val jar = getRobotLib()
    classpath(project.files(jar))

    // adding packed jar
    val packJar = project.tasks.getByName("jar") as Jar
    classpath(packJar.archiveFile.get())

    // TODO Create switch to choose between jar or classes
//    val jpc = project.convention.getPlugin(JavaPluginConvention::class.java)
//    classpath(jpc.sourceSets.getByName("main").output.classesDirs)
//    classpath(jpc.sourceSets.getByName("main").output.resourcesDir)

    // adding dependencies
    classpath(
      project.configurations.getByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)
        .resolvedConfiguration.resolvedArtifacts.map { it.file }.toTypedArray()
    )
  }

  private fun getRobotLib(): File? {
    val rfVersion = rfExtension.robotVersion.get()
    val artifacts: Set<ResolvedArtifact> = project.configurations
      .getByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)
      .resolvedConfiguration.resolvedArtifacts
    return artifacts.find {
      val id = it.moduleVersion.id
      // TODO make version-comparison more flexible e.g. allow >=
      id.version == rfVersion.version &&
        id.group == rfVersion.group &&
        id.name == rfVersion.name
    }?.file
  }
}
