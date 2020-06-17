package de.qualersoft.robotframework.gradleplugin.configurations

import de.qualersoft.robotframework.gradleplugin.utils.Arguments
import de.qualersoft.robotframework.gradleplugin.utils.GradleNullableProperty
import de.qualersoft.robotframework.gradleplugin.utils.GradleProperty
import de.qualersoft.robotframework.gradleplugin.utils.GradleStringListProperty
import de.qualersoft.robotframework.gradleplugin.utils.GradleStringMapProperty
import org.gradle.api.Project

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
class RunRobotConfiguration(project: Project) : BotRobotConfiguration(project) {

  companion object {
    const val MAX_ERROR_LINES = 40
    const val CONSOLE_WIDTH = 78
  }

  //<editor-fold desc="run specific configuration properties">
  /**
   * Parse only files with this extension when executing
   * a directory. Has no effect when running individual
   * files or when using resource files. If more than one
   * extension is needed, separate them with a colon.
   *
   * Examples:
   * ```
   * extension = "txt"
   * extension = "robot:txt"
   *```
   * @since RF 3.0.1. Starting from RF 3.2 only `*.robot`
   * files are parsed by default.
   */
  @Suppress("private")
  var extension by GradleStringListProperty(project)

  /**
   * Select failed tests from an earlier output file to be
   * re-executed. Equivalent to selecting same tests
   * individually using [test].
   */
  @Suppress("private")
  var rerunFailed by GradleNullableProperty(project, String::class)

  /**
   * Select failed suites from an earlier output
   * file to be re-executed.
   *
   * @since RF 3.0.1.
   */
  @Suppress("private")
  var rerunFailedSuites by GradleNullableProperty(project, String::class)

  /**
   * Set variables in the test data. Only scalar
   * variables with string value are supported and name is
   * given without `${}`. See [variableFile] for a more
   * powerful variable setting mechanism.
   *
   * TODO example line 2: What is `E` parameter? -> Replace with property
   *
   * Examples:
   * ```
   * variable = mapOf("str" to "Hello")               =>  ${str} = `Hello`
   * variable = mapOf("hi" to "Hi_World") -E space:_  =>  ${hi} = `Hi World`
   * variable = mapOf("x" to null, "y" to "42")       =>  ${x} = ``, ${y} = `42`
   * ```
   */
  @Suppress("private")
  var variable by GradleStringMapProperty(project)

  /**
   * Python or YAML file file to read variables from.
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
  var variableFile by GradleStringListProperty(project)

  /**
   * Debug file written during execution. Not created
   * unless this option is specified.
   */
  @Suppress("private")
  var debugFile by GradleNullableProperty(project, String::class)

  /**
   * Maximum number of error message lines to show in
   * report when tests fail. Default is 40, minimum is 10
   * and -1 can be used to show the full message (will be mapped to `NONE` in cmd call).
   */
  @Suppress("private")
  var maxErrorLines by GradleProperty(project, Int::class, MAX_ERROR_LINES)

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
  var listener by GradleStringListProperty(project)

  /**
   * Verifies test data and runs tests so that library
   * keywords are not executed.
   *
   * Default: `false`
   */
  @Suppress("private")
  var dryrun by GradleProperty(project, Boolean::class, false)

  /**
   * Stops test execution if any critical test fails.
   *
   * Default: `false`
   */
  @Suppress("private")
  var exitOnFailure by GradleProperty(project, Boolean::class, false)

  /**
   * Stops test execution if any error occurs when parsing
   * test data, importing libraries, and so on.
   *
   * Default: `false`
   */
  @Suppress("private")
  var exitOnError by GradleProperty(project, Boolean::class, false)

  /**
   * Causes teardowns to be skipped if test execution is
   * stopped prematurely.
   *
   * Default: `false`
   */
  @Suppress("private")
  var skipTearDownOnExit by GradleProperty(project, Boolean::class, false)

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
  var randomize by GradleProperty(project, String::class, "none")

  /**
   * Class to programmatically modify the test suite
   * structure before execution.
   */
  @Suppress("private")
  var preRunModifier by GradleStringListProperty(project)

  /**
   * Class to programmatically modify the result
   * model before creating reports and logs.
   */
  @Suppress("private")
  var preRebotModifier by GradleStringListProperty(project)

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
  var console by GradleProperty(project, String::class, "verbose")

  /**
   * Shortcut for `console = "dotted"`.
   */
  @Suppress("private")
  var dotted by GradleProperty(project, Boolean::class, false)

  /**
   * Shortcut for `console = "quite"`.
   */
  @Suppress("private")
  var quite by GradleProperty(project, Boolean::class, false)

  /**
   * Width of the console output.
   *
   * Default: `78`.
   */
  @Suppress("private")
  var consoleWidth by GradleProperty(project, Int::class, CONSOLE_WIDTH)

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
  var consoleMarkers by GradleProperty(project, String::class, "auto")

  /**
   * Robot Framework test cases are created in
   * [files][http://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html#test-case-files] and
   * [directories][http://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html#test-suite-directories]
   * , and they are executed by giving the path to
   * the file or directory in question to the selected runner script.
   * The path can be absolute or, more commonly, relative to the
   * directory where tests are executed from. The given file or
   * directory creates the top-level test suite, which gets its
   * name, unless overridden with the [name]&nbsp;
   * [option][http://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html#setting-the-name],
   * from the [file or directory name]
   * [http://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html#test-suite-name-and-documentation].
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
   * In most cases, it is thus better to use the [name] option for
   * overriding it, as in the second example below:
   * ```
   * robot my_tests.robot your_tests.robot
   * robot --name Example path/to/tests/pattern_*.robot
   * ```
   */
  @Suppress("private")
  var dataSources by GradleStringListProperty(project)
  //</editor-fold>

  override fun generateArguments(): Array<String> = Arguments().apply {
    addArgs(super.generateArguments())
    addListToArguments(extension, "-F")
    addStringToArguments(rerunFailed, "-R")
    addStringToArguments(rerunFailedSuites, "-S")
    addMapToArguments(variable, "-v")
    addStringToArguments(debugFile, "-b")

    if (0 > maxErrorLines) {
      addStringToArguments("NONE", "--maxerrorlines")
    } else {
      addStringToArguments(maxErrorLines.toString(), "--maxerrorlines")
    }

    addListToArguments(listener, "--listener")

    addFlagToArguments(dryrun, "--dryrun")
    addFlagToArguments(exitOnFailure, "-X")
    addFlagToArguments(exitOnError, "--exitonerror")
    addFlagToArguments(skipTearDownOnExit, "--skipteardownonexit")
    addListToArguments(randomize, "--randomize")
    addListToArguments(preRunModifier, "--prerunmodifier")
    addListToArguments(preRebotModifier, "--prerebotmodifier")
    addStringToArguments(console, "--console")
    addFlagToArguments(dotted, "-.")
    addFlagToArguments(quite, "--quite")
    addStringToArguments(consoleWidth.toString(), "-W")
    addStringToArguments(consoleMarkers, "-K")
    addArgs(dataSources.toTypedArray())
  }.toArray()
}