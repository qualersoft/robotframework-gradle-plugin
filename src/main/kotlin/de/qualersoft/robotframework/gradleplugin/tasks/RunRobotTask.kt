package de.qualersoft.robotframework.gradleplugin.tasks

import de.qualersoft.robotframework.gradleplugin.configurations.RunRobotConfiguration
import org.gradle.api.Action
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

open class RunRobotTask : BasicRobotFrameworkTask() {

  init {
    description = "Runs the robot tests"
    group = "verification"
  }

  /**
   * Robot Framework test cases are created in
   * [files](http://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html#test-case-files) and
   * [directories](http://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html#test-suite-directories)
   * , and they are executed by giving the path to
   * the file or directory in question to the selected runner script.
   * The path can be absolute or, more commonly, relative to the
   * directory where tests are executed from. The given file or
   * directory creates the top-level test suite, which gets its
   * name, unless overridden with the [RunRobotConfiguration.name]&nbsp;
   * [option](http://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html#setting-the-name),
   * from the [file or directory name](http://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html#test-suite-name-and-documentation).
   *
   * Different execution possibilities are illustrated in the
   * examples below. Note that in these examples, as well as
   * in other examples in this section, only the robot script
   * is used, but other execution approaches could be used similarly.
   * ```
   * robot tests.robot
   * robot path/to/my_tests/
   * robot c:\robot\tests.robot
   * ```
   * It is also possible to give paths to several test case files
   * or directories at once, separated with spaces. In this case,
   * Robot Framework creates the top-level test suite automatically,
   * and the specified files and directories become its child test
   * suites. The name of the created test suite is got from child
   * suite names by concatenating them together with an ampersand (&)
   * and spaces. For example, the name of the top-level suite in
   * the first example below is *My Tests & Your Tests*. These
   * automatically created names are often quite long and complicated.
   * In most cases, it is thus better to use the [RunRobotConfiguration.name] option for
   * overriding it, as in the second example below:
   * ```
   * robot my_tests.robot your_tests.robot
   * robot --name Example path/to/tests/pattern_*.robot
   * ```
   */
  @InputFiles
  @PathSensitive(PathSensitivity.ABSOLUTE)
  var sources: FileCollection = objectFactory.fileCollection()

  private val robot = objectFactory.property(RunRobotConfiguration::class.java)
    .convention(rfExtension.robot)

  /**
   * Directory where the output shall be put to
   */
  @OutputDirectory
  val outputDir: DirectoryProperty = objectFactory.directoryProperty()
    .convention(robot.get().outputDir)

  fun robot(action: Action<RunRobotConfiguration>) {
    action.execute(robot.get())
  }

  fun robot(config: RunRobotConfiguration.() -> Unit) {
    robot.get().apply(config)
  }

  override fun exec() {
    val srcFile = sources.files.map { it.absolutePath }

    rfArgs = rfArgs + robot.get().generateArguments().toList()
    println("RRT: generated arguments: $rfArgs")
    super.executeRobotCommand("run", srcFile)
  }
}