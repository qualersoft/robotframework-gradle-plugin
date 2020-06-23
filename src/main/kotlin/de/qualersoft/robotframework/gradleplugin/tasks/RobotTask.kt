package de.qualersoft.robotframework.gradleplugin.tasks

import de.qualersoft.robotframework.gradleplugin.configurations.RunRobotConfiguration
import de.qualersoft.robotframework.gradleplugin.utils.GradleProperty
import org.gradle.api.Action
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

open class RobotTask : BasicRobotFrameworkTask() {

  /**
   * File(s) to be passed to the run command
   */
  @InputFiles
  @PathSensitive(PathSensitivity.ABSOLUTE)
  var sources: FileCollection = project.objects.fileCollection().from(extension.robot.dataSources)

  /**
   * Directory where the output shall be put to
   */
  @OutputDirectory
  val outputDir: DirectoryProperty = project.objects.directoryProperty()
          .convention(project.provider { robot.outputDir.get() })

  private var robot by GradleProperty(project, RunRobotConfiguration::class, RunRobotConfiguration(project))
  fun robot(action: Action<RunRobotConfiguration>) {
    action.execute(robot)
  }
  fun robot(config: RunRobotConfiguration.() -> Unit) {
    robot.apply(config)
  }

  override fun exec() {
    val srcFile = sources.files.map { it.absolutePath }
    rfArgs = (robot.generateArguments().toList() + rfArgs) as MutableList<String>
    super.executeRobotCommand("run", srcFile)
  }
}