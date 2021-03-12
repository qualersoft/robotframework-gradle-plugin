package de.qualersoft.robotframework.gradleplugin.configurations

import de.qualersoft.robotframework.gradleplugin.utils.Arguments
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

/**
 * ## From robot help
 * Robot Framework is a generic open source automation framework for acceptance
 * testing, acceptance test-driven development (ATDD) and robotic process
 * automation (RPA). It has simple, easy-to-use syntax that utilizes the
 * keyword-driven automation approach. Keywords adding new capabilities are
 * implemented in libraries using either Python or Java. New higher level
 * keywords can also be created using Robot Framework's own syntax.
 *
 * The easiest way to execute Robot Framework is using the `robot` command created
 * as part of the normal installation. Alternatively it is possible to execute
 * the `robot` module directly using `python -m robot`, where `python` can be
 * replaced with any supported Python interpreter such as `jython`, `ipy` or
 * `python3`. Yet another alternative is running the `robot` directory like
 * `python path/to/robot`. Finally, there is a standalone JAR distribution
 * available.
 *
 * Tests (or tasks in RPA terminology) are created in files typically having the
 * `*.robot` extension. Files automatically create test (or task) suites and
 * directories with these files create higher level suites. When Robot Framework
 * is executed, paths to these files or directories are given to it as arguments.
 *
 * By default Robot Framework creates an XML output file and a log and a report in
 * HTML format, but this can be configured using various options listed below.
 * Outputs in HTML format are for human consumption and XML output for integration
 * with other systems. XML outputs can also be combined and otherwise further
 * post-processed with the Rebot tool that is an integral part of Robot Framework.
 * Run `rebot --help` for more information.
 *
 * Robot Framework is open source software released under Apache License 2.0.
 * For more information about the framework and the rich ecosystem around it
 * see [http://robotframework.org/].
 *
 * Repetitive options can be specified multiple times.
 * For example, `--test first --test third` selects test cases with name `first`
 * and `third`. If an option accepts a value but is not marked with an asterisk,
 * the last given value has precedence. For example, `--log A.html --log B.html`
 * creates log file `B.html`. Options accepting no values can be disabled by
 * using the same option again with `no` prefix added or dropped. The last option
 * has precedence regardless of how many times options are used. For example,
 * `--dryrun --dryrun --nodryrun --nostatusrc --statusrc` would not activate the
 * dry-run mode and would return a normal return code.
 *
 * Long option format is case-insensitive. For example, `--SuiteStatLevel` is
 * equivalent to but easier to read than `--suitestatlevel`. Long options can
 * also be shortened as long as they are unique. For example, `--logti Title`
 * works while `--lo log.html` does not because the former matches only `--logtitle`
 * but the latter matches `--log`, `--loglevel` and `--logtitle`.
 *
 * ## Environment Variables
 * - **ROBOT_OPTIONS**: Space separated list of default options to be placed
 * in front of any explicit options on the command line.
 * - **ROBOT_SYSLOG_FILE**: Path to a file where Robot Framework writes internal
 * information about parsing test case files and running
 * tests. Can be useful when debugging problems. If not
 * set, or set to a special value `NONE`, writing to the
 * syslog file is disabled.
 * - **ROBOT_SYSLOG_LEVEL**: Log level to use when writing to the syslog file.
 * Available levels are the same as with --loglevel
 * command line option and the default is INFO.
 * - **ROBOT_INTERNAL_TRACES**: When set to any non-empty value, Robot Framework's
 * internal methods are included in error tracebacks.
 *
 * ## Examples
 * ```
 * # Simple test run using 'robot' command without options.
 * $ robot tests.robot
 *
 * # Using options.
 * $ robot --include smoke --name "Smoke Tests" path/to/tests.robot
 *
 * #Executing 'robot' module using Python.
 * $ python -m robot path/to/tests
 *
 * # Running 'robot' directory with Jython.
 * $ jython /opt/robot tests.robot
 *
 * # Executing multiple test case files and using case-insensitive long options.
 * $ robot --SuiteStatLevel 2 --Metadata Version:3 tests/*.robot more/tests.robot
 *
 * # Setting default options and syslog file before running tests.
 * $ export ROBOT_OPTIONS="--critical regression --suitestatlevel 2"
 * $ export ROBOT_SYSLOG_FILE=/tmp/syslog.txt
 * $ robot tests.robot
 * ```
 */*/
class RunRobotConfiguration @Inject constructor(project: Project) : BotRobotConfiguration(project) {

  //<editor-fold desc="run specific configuration properties">
  /**
   * Parse only files with this extension when executing
   * a directory. Has no effect when running individual
   * files or when using resource files.
   *
   * Examples:
   * ```
   * extension.add("txt")
   * extension.addAll("robot", "txt")
   *```
   * @since RF 3.0.1. Starting from RF 3.2 only `*.robot`
   * files are parsed by default.
   */
  @Suppress("private")
  val extension: ListProperty<String> = objects.listProperty(String::class.java).convention(mutableListOf("robot"))

  /**
   * Set variables in the test data. Only scalar
   * variables with string value are supported and name is
   * given without `${}`. See [variableFiles] for a more
   * powerful variable setting mechanism.
   *
   * Examples:
   * ```
   * variable = mapOf("str" to "Hello")           =>  ${str} = `Hello`
   * variable = mapOf("hi" to "Hi_World")         =>  ${hi} = `Hi World` (*)
   * variable = mapOf("x" to null, "y" to "42")   =>  ${x} = ``, ${y} = `42`
   * ```
   * Remarks:
   * * (*) To replaces _ in `Hi_World` with `space` pass `-E space:_` as additional argument
   * * Prefer [variableFiles]
   */
  @Suppress("private")
  val variables: MapProperty<String, String> = objects.mapProperty(String::class.java, String::class.java)

  /**
   * Python or YAML file to read variables from.
   * Possible arguments to the variable file can be given
   * after the path using colon or semicolon as separator.
   *
   * Examples:
   * ```
   * variableFile = "path/vars.yaml"
   * variableFile = "environment.py:testing"
   * ```
   */
  @Suppress("private")
  val variableFiles: ListProperty<String> = objects.listProperty(String::class.java)

  /**
   * Debug file written during execution. Not created
   * unless this option is specified.
   */
  @Suppress("private")
  val debugFile: RegularFileProperty = objects.fileProperty()

  /**
   * Maximum number of error message lines to show in
   * report when tests fail. Default is `40`, minimum is `10`
   * and `-1` can be used to show the full message (will be mapped to `NONE` in cmd call).
   *
   * __Remark__: The lower limit of `10` is not validated by the plugin!
   */
  @Suppress("private")
  val maxErrorLines: Property<Int> = objects.property(Int::class.java).convention(MAX_ERROR_LINES)

  /**
   * A class for monitoring test execution. Gets
   * notifications e.g. when tests start and end.
   * Arguments to the listener class can be given after
   * the name using a colon or a semicolon as a separator.
   *
   * Examples:
   * ```
   * listener = listOf("MyListenerClass")
   * listener = listOf("path/to/Listener.py:arg1:arg2")
   * ```
   */
  @Suppress("private")
  val listener: ListProperty<String> = objects.listProperty(String::class.java)

  /**
   * Verifies test data and runs tests so that library
   * keywords are not executed.
   *
   * Default: `false`
   */
  @Suppress("private")
  val dryrun: Property<Boolean> = objects.property(Boolean::class.java).convention(false)

  /**
   * Stops test execution if any critical test fails.
   *
   * Default: `false`
   */
  @Suppress("private")
  val exitOnFailure: Property<Boolean> = objects.property(Boolean::class.java).convention(false)

  /**
   * Stops test execution if any error occurs when parsing
   * test data, importing libraries, and so on.
   *
   * Default: `false`
   */
  @Suppress("private")
  val exitOnError: Property<Boolean> = objects.property(Boolean::class.java).convention(false)

  /**
   * Causes teardowns to be skipped if test execution is
   * stopped prematurely.
   *
   * Default: `false`
   */
  @Suppress("private")
  val skipTearDownOnExit: Property<Boolean> = objects.property(Boolean::class.java).convention(false)

  /**
   * Randomizes the test execution order.
   * + **all**:    randomizes both suites and tests
   * + **suites**: randomizes suites
   * + **tests**:  randomizes tests
   * + **none**:   no randomization
   *
   * Use syntax `VALUE:SEED` to give a custom random seed.
   * The seed must be an integer.
   *
   * Examples:
   * ```
   * randomize = "all"
   * randomize = "tests:1234"
   * ```
   * Default: `none`
   */
  @Suppress("private")
  val randomize: Property<String> = objects.property(String::class.java).convention("none")

  /**
   * Class to programmatically modify the test suite
   * structure before execution.
   */
  @Suppress("private")
  val preRunModifier: ListProperty<String> = objects.listProperty(String::class.java)

  /**
   * Class to programmatically modify the result
   * model before creating reports and logs.
   */
  @Suppress("private")
  val preRebotModifier: ListProperty<String> = objects.listProperty(String::class.java)

  /**
   * How to report execution on the console.
   * + **verbose**:  report every suite and test
   * + **dotted**:   only show '.' for passed test,
   *
   *                 'f' for failed non-critical tests, and
   *
   *                 'F' for failed critical tests
   * + **quiet**:    no output except for errors and warnings
   * + **none**:     no output whatsoever
   *
   * Default: `verbose`
   */
  @Suppress("private")
  val console: Property<String> = objects.property(String::class.java).convention( "verbose")

  /**
   * Shortcut for `console = "dotted"`.
   *
   * Default: `false`
   */
  @Suppress("private")
  val dotted: Property<Boolean> = objects.property(Boolean::class.java).convention(false)

  /**
   * Shortcut for `console = "quite"`.
   *
   * Default: `false`
   */
  @Suppress("private")
  val quite: Property<Boolean> = objects.property(Boolean::class.java).convention(false)

  /**
   * Width of the console output.
   *
   * Default: `78`.
   */
  @Suppress("private")
  val consoleWidth: Property<Int> = objects.property(Int::class.java).convention(CONSOLE_WIDTH)

  /**
   * Use colors on console output or not.
   * + **auto**: use colors when output not redirected
   * + **on**:   always use colors
   * + **ansi**: like 'on' but use ANSI colors also on Windows
   * + **off**:  disable colors altogether
   *
   * Note that colors do not work with Jython on Windows.
   *
   * Default: `auto`
   */
  @Suppress("private")
  val consoleMarkers: Property<String> = objects.property(String::class.java).convention("auto")
  //</editor-fold>

  override fun generateArguments(): Array<String> = Arguments().apply {
    addArgs(super.generateArguments())
    addListToArguments(extension.orNull, "-F")
    addMapToArguments(variables.get(), "-v")
    addListToArguments(variableFiles.orNull, "-V")
    addStringToArguments(debugFile.orNull?.let { it.asFile.absolutePath }, "-b")

    if (0 > maxErrorLines.get()) {
      addStringToArguments("NONE", "--maxerrorlines")
    } else {
      addStringToArguments(maxErrorLines.get().toString(), "--maxerrorlines")
    }

    addListToArguments(listener.orNull, "--listener")

    addFlagToArguments(dryrun.get(), "--dryrun")
    addFlagToArguments(exitOnFailure.get(), "-X")
    addFlagToArguments(exitOnError.get(), "--exitonerror")
    addFlagToArguments(skipTearDownOnExit.get(), "--skipteardownonexit")
    addListToArguments(randomize.orNull, "--randomize")
    addListToArguments(preRunModifier.orNull, "--prerunmodifier")
    addListToArguments(preRebotModifier.orNull, "--prerebotmodifier")

    addStringToArguments(console.orNull, "--console")
    addFlagToArguments(dotted.get(), "-.")
    addFlagToArguments(quite.get(), "--quite")
    addStringToArguments(consoleWidth.get().toString(), "-W")
    addStringToArguments(consoleMarkers.get(), "-K")
  }.toArray()

  companion object {
    const val MAX_ERROR_LINES = 40
    const val CONSOLE_WIDTH = 78
  }
}