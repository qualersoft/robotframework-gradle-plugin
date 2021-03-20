package de.qualersoft.robotframework.gradleplugin.tasks

import de.qualersoft.robotframework.gradleplugin.configurations.TidyRobotConfiguration
import de.qualersoft.robotframework.gradleplugin.robotframework
import de.qualersoft.robotframework.gradleplugin.utils.GradleProperty
import org.gradle.api.Action
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import java.io.File

open class TidyTask : BasicRobotFrameworkTask() {
  init {
    description = "Tidy tool can be used to clean up Robot Framework data."
    group = "robot"
  }

  private val tidy = project.objects.property(TidyRobotConfiguration::class.java)
    .convention(project.robotframework().tidy)

  fun tidy(action: Action<TidyRobotConfiguration>) {
    action.execute(tidy.get())
  }

  fun tidy(config: TidyRobotConfiguration.() -> Unit) {
    tidy.get().apply(config)
  }

  @InputFiles
  @PathSensitive(PathSensitivity.ABSOLUTE)
  var sources: FileCollection = project.objects.fileCollection()

  /**
   * Filename of the cleaned file.
   * Only appropriate if single file is processed.
   */
  var outputFile by GradleProperty(project.objects, File::class)

  override fun exec() {
    rfArgs = (tidy.get().generateArguments().toList() + rfArgs)
    val srcFiles = sources.files.joinToString(" ") { it.path }
    val args = mutableListOf(srcFiles)
    if (outputFile.isPresent) {
      args.add(outputFile.get().absolutePath)
    }
    super.executeRobotCommand("tidy", args)
  }
}