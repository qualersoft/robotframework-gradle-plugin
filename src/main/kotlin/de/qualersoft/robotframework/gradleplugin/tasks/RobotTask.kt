package de.qualersoft.robotframework.gradleplugin.tasks

import de.qualersoft.robotframework.gradleplugin.configurations.RunRobotConfiguration
import de.qualersoft.robotframework.gradleplugin.extensions.RobotFrameworkExtension
import de.qualersoft.robotframework.gradleplugin.utils.GradleProperty
import org.gradle.api.Action
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction


open class RobotTask : BasicRobotFrameworkTask {

  constructor() {
    val rfExt = project.extensions.getByType(RobotFrameworkExtension::class.java)
    mainClass.set(rfExt.robotVersion.mainClass)
  }

  /**
   * File(s) to be passed to the run command
   */
  @InputFiles
  var sources: FileTree = project.objects.fileTree()

  var outputDir: DirectoryProperty = project.objects.directoryProperty()

  var robot by GradleProperty(project, RunRobotConfiguration::class, RunRobotConfiguration(project))
  @Suppress("Unused")
  fun robot(action: Action<RunRobotConfiguration>) {
    action.execute(robot)
  }
  @Suppress("Unused")
  fun robot(config: RunRobotConfiguration.() -> Unit) {
    robot.apply(config)
  }

  @TaskAction
  override fun exec() {
    val srcFile = sources.files.map { it.absolutePath }
    rfArgs = (robot.generateArguments().toMutableList() + rfArgs) as MutableList<String>
    super.executeRobotCommand("run", *srcFile.toTypedArray())
  }
}