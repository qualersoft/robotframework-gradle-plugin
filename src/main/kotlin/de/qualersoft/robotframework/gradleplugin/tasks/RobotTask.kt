package de.qualersoft.robotframework.gradleplugin.tasks

import de.qualersoft.robotframework.gradleplugin.configurations.RunRobotConfiguration
import de.qualersoft.robotframework.gradleplugin.utils.GradleProperty
import org.gradle.api.Action
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction


open class RobotTask : BasicRobotFrameworkTask() {

  /**
   * File(s) to be passed to the run command
   * TODO: Link to robot property as default supplier
   */
  @InputFiles
  @PathSensitive(PathSensitivity.ABSOLUTE)
  var sources: FileTree = project.objects.fileTree()

  /**
   * Directory where the output shall be put to
   * TODO: Link to robot property as default supplier
   */
  @OutputDirectory
  val outputDir: DirectoryProperty = project.objects.directoryProperty()
    .fileProvider(project.provider { robot.outputDir.get().asFile })

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