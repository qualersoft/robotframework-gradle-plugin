package de.qualersoft.robotframework.gradleplugin.tasks

import de.qualersoft.robotframework.gradleplugin.configurations.TidyRobotConfiguration
import org.gradle.api.Action
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

open class TidyTask : BasicRobotFrameworkTask() {
  init {
    description = "Tidy tool can be used to clean up Robot Framework data."
    group = "robot"
  }

  private val tidy = objectFactory.property(TidyRobotConfiguration::class.java)
    .convention(rfExtension.tidy)

  fun tidy(action: Action<TidyRobotConfiguration>) {
    action.execute(tidy.get())
  }

  fun tidy(config: TidyRobotConfiguration.() -> Unit) {
    tidy.get().apply(config)
  }

  @InputFiles
  @PathSensitive(PathSensitivity.ABSOLUTE)
  var sources: FileCollection = objectFactory.fileCollection()

  /**
   * Filename of the cleaned file.
   * Only appropriate if single file is processed.
   */
  @OutputFile
  @Optional
  val outputFile = objectFactory.fileProperty()

  override fun exec() {
    rfArgs = rfArgs + tidy.get().generateArguments().toList()
    val srcFiles = sources.files.joinToString(" ") { it.path }
    val args = mutableListOf(srcFiles)
    if (outputFile.isPresent) {
      args.add(outputFile.asFile.get().absolutePath)
    }
    super.executeRobotCommand("tidy", args)
  }
}
