package de.qualersoft.robotframework.gradleplugin.tasks

import de.qualersoft.robotframework.gradleplugin.configurations.TestdocRobotConfiguration
import de.qualersoft.robotframework.gradleplugin.utils.GradleProperty
import org.gradle.api.Action
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import java.io.File

/**
 * Testdoc generates a high level test documentation based on Robot Framework
 * test data. Generated documentation includes name, documentation and other
 * metadata of each test suite and test case, as well as the top-level keywords
 * and their arguments.
 *
 * Data can be given as a single file, directory, or as multiple files and
 * directories. In all these cases, the last argument must be the file where
 * to write the output. The output is always created in HTML format.
 */
open class TestdocTask : BasicRobotFrameworkTask() {
  init {
    description = "Generates a high level test documentation based on Robot Framework test data."
    group = "documentation"
  }

  private val testdoc = objectFactory.property(TestdocRobotConfiguration::class.java)
    .convention(rfExtension.testdoc)

  fun testdoc(action: Action<TestdocRobotConfiguration>) {
    action.execute(testdoc.get())
  }

  fun testdoc(config: TestdocRobotConfiguration.() -> Unit) {
    testdoc.get().apply(config)
  }

  @InputFiles
  @PathSensitive(PathSensitivity.ABSOLUTE)
  var sources: FileCollection = objectFactory.fileCollection()

  /**
   * Directory to which to put the generated documentation.
   *
   * *Default*: `${buildDir}/doc`
   */
  @OutputDirectory
  val outputDir: DirectoryProperty = objectFactory.directoryProperty()
    .convention(testdoc.get().outputDir)

  /**
   * Filename of the generated documentation.
   *
   * *Default*: `testdoc.html`
   */
  var outputFile by GradleProperty(objectFactory, File::class, testdoc.get().outputFile)

  override fun exec() {
    rfArgs = rfArgs + testdoc.get().generateArguments().toList()
    val srcFiles = sources.files.joinToString(" ") { it.path }
    val dest = outputDir.file(outputFile.get().toString()).get().asFile.absolutePath
    super.executeRobotCommand("testdoc", listOf(srcFiles, dest))
  }
}