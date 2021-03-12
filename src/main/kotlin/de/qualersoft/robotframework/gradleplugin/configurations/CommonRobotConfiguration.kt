package de.qualersoft.robotframework.gradleplugin.configurations

import de.qualersoft.robotframework.gradleplugin.utils.Arguments
import de.qualersoft.robotframework.gradleplugin.utils.GradleFileNullableProperty
import de.qualersoft.robotframework.gradleplugin.utils.GradleProperty
import de.qualersoft.robotframework.gradleplugin.utils.GradleStringListProperty
import de.qualersoft.robotframework.gradleplugin.utils.GradleStringMapProperty
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import java.io.File
import javax.inject.Inject

open class BotRobotConfiguration(project: Project) : CommonRobotConfiguration(project.objects) {

  //<editor-fold desc="properties">

  /**
   * Turn on the generic automation mode (aka **R**obot **P**rocess **A**utomation).
   * Mainly affects terminology so that "test" is replaced with "task" in logs
   * and reports. By default the mode is got from the processed output files.
   * @since RF 3.1.
   */
  @Suppress("private")
  var rpa by GradleProperty(objects, Boolean::class, false)

  /**
   * Set the documentation of the top level suite.
   * Simple formatting is supported (e.g. &#42;bold&#42;). If
   * the documentation contains spaces, it must be quoted.
   * ### Example:
   *    doc = "Very *good* example"
   */
  @Suppress("private")
  var doc by GradleProperty(objects, String::class)

  /**
   * Set metadata of the top level suite. Value can contain formatting similarly as [doc].
   * ### Example:
   *    metaData = mapOf("Version" to "1.2")
   */
  @Suppress("private")
  var metaData by GradleStringMapProperty(objects)

  /**
   * Sets given tag(s) to all tests.
   */
  @Suppress("private")
  var setTags by GradleStringListProperty(objects)

  /**
   * Select tests by name or by long name containing also
   * parent suite name like `Parent.Test`. Name is case
   * and space insensitive and it can also be a simple
   * pattern where `*` matches anything, `?` matches any
   * single character, and `[chars]` matches one character
   * in brackets.
   */
  @Suppress("private")
  var test by GradleStringListProperty(objects)

  /**
   * Alias to [test]. Especially applicable with [rpa].
   */
  @Suppress("private")
  var task by GradleStringListProperty(objects)

  /**
   * Select suites by name. When this option is used with
   * [test], [include] or [exclude], only tests in
   * matching suites and also matching other filtering
   * criteria are selected. Name can be a simple pattern
   * similarly as with [test] and it can contain parent
   * name separated with a dot. For example, `suite = "X.Y"`
   * selects suite `Y` only if its parent is `X`.
   */
  @Suppress("private")
  var suite by GradleStringListProperty(objects)

  /**
   * Select tests by tag. Similarly as name with [test],
   * tag is case and space insensitive and it is possible
   * to use patterns with `*`, `?` and `[]` as wildcards.
   * Tags and patterns can also be combined together with
   * `AND`, `OR`, and `NOT` operators.
   *
   * ### Examples:
   *    include = listOf("foo", "bar*")
   *    include = "fooANDbar*"
   */
  @Suppress("private")
  var include by GradleStringListProperty(objects)

  /**
   * Specify tests not to be included by tag. They are not
   * selected even if included with [include]. Tags are
   * matched using same rules as with [include].
   */
  @Suppress("private")
  var exclude by GradleStringListProperty(objects)

  /**
   * Tests having the given tag are considered critical.
   * If no critical tags are set, all tests are critical.
   * Tags can be given as a pattern same way as with
   * [include].
   */
  @Suppress("private")
  var critical by GradleStringListProperty(objects)

  /**
   * Tests having the given tag are not critical even if
   * they have a tag set with [critical]. Tag can be
   * a pattern.
   */
  @Suppress("private")
  var nonCritical by GradleStringListProperty(objects)

  /**
   * Where to create output files. The given path is considered
   * relative to command execution directory unless it is absolute.
   *
   * Default: `${project.buildDir}/reports/robotframework`.
   */
  @Suppress("private")
  val outputDir: DirectoryProperty = objects.directoryProperty()
    .convention(
      project.layout.buildDirectory.dir(joinPaths("reports", "robotframework"))
    )

  /**
   * XML output file. Not created unless this option is
   * specified. Given path, similarly as paths given to
   * [log], [report] and [xUnit], is relative to
   * [outputDir] unless given as an absolute path.
   */
  @Suppress("private")
  var output by GradleFileNullableProperty(objects)

  /**
   * HTML log file. Can be disabled by passing `null`.
   *
   * Default: `log.html`
   *
   * ### Examples:
   *    log = "mylog.html"
   *    log = null
   */
  @Suppress("private")
  var log by GradleFileNullableProperty(objects, File("log.html"))

  /**
   * HTML report file. Can be disabled by passing `null`
   * similarly as [log].
   *
   * Default: `report.html`
   */
  @Suppress("private")
  var report by GradleFileNullableProperty(objects, File("report.html"))

  /**
   * xUnit compatible result file. Not created unless this
   * option is specified. Will be stored under [outputDir].
   * Default: `robot-xunit-results.xml`
   */
  @Suppress("private")
  var xUnit by GradleFileNullableProperty(objects, File("robot-xunit-results.xml"))

  /**
   * Mark non-critical tests in xUnit output as skipped.
   *
   * Default: disabled (`false`)
   */
  @Suppress("private")
  var xUnitSkipNonCritical by GradleProperty(objects, Boolean::class, false)

  /**
   * When this option is used, timestamp in a format
   * `YYYYMMDD-hhmmss` is added to all generated output
   * files between their basename and extension.
   *
   * ### For example:
   *    timestampOutputs = true
   *    output = "output.xml"
   *    report = "report.html"
   *    log = null
   *
   * creates files like `output-20070503-154410.xml` and
   * `report-20070503-154410.html`.
   *
   * Default: `false`
   */
  @Suppress("private")
  var timestampOutputs by GradleProperty(objects, Boolean::class, false)

  /**
   * Split the log file into smaller pieces that open in
   * browsers transparently.
   *
   * Default: `false`
   */
  @Suppress("private")
  var split by GradleProperty(objects, Boolean::class, false)

  /**
   * Title for the generated log file. The default title
   * is `<SuiteName> Test Log`.
   */
  @Suppress("private")
  var logTitle by GradleProperty(objects, String::class)

  /**
   * Title for the generated report file. The default
   * title is `<SuiteName> Test Report`.
   */
  @Suppress("private")
  var reportTitle by GradleProperty(objects, String::class)

  /**
   * Background colors to use in the report file.
   * Either `all_passed:critical_passed:failed` or
   * `passed:failed`. Both color names and codes work.
   *
   * ### Examples:
   *    reportBackground = "green:yellow:red"
   *    reportBackground = "#00E:#E00"
   */
  @Suppress("private")
  var reportBackground by GradleProperty(objects, String::class)

  /**
   * The threshold level for logging.
   * Threshold for selecting messages. Available levels:
   * TRACE (default), DEBUG, INFO, WARN, NONE (no msgs).
   * Use syntax `LOGLEVEL:DEFAULT` to define the default
   * visible log level in log files.
   *
   * ### Examples:
   *    logLevel = "DEBUG"
   *    logLevel = "DEBUG:INFO"
   */
  @Suppress("private")
  var logLevel by GradleProperty(objects, String::class)

  /**
   * How many levels to show in `Statistics by Suite`
   * in log and report. By default all suite levels are
   * shown.
   * ### Example:
   *    suiteStatLevel = 3
   */
  @Suppress("private")
  var suiteStatLevel by GradleProperty(objects, Int::class)

  /**
   * Include only matching tags in `Statistics by Tag`
   * in log and report. By default all tags are shown.
   * Given tag can be a pattern like with [include].
   */
  @Suppress("private")
  var tagStatInclude by GradleStringListProperty(objects)

  /**
   * Exclude matching tags from `Statistics by Tag`.
   * This option can be used with [tagStatInclude]
   * similarly as [exclude] is used with [include].
   */
  @Suppress("private")
  var tagStatExclude by GradleStringListProperty(objects)

  /**
   * Create combined statistics based on tags.
   * These statistics are added into `Statistics by Tag`.
   * If the optional [name] is not given, name of the
   * combined tag is got from the specified tags. Tags are
   * matched using the same rules as with [include].
   * ### Examples:
   *    tagstatcombine = mapOf("requirement-*" to null)
   *    tagstatcombine = mapOf("tag1ANDtag2" to "My_name")
   */
  @Suppress("private")
  var tagStatCombine by GradleStringMapProperty(objects)

  /**
   * Add documentation to tags matching the given
   * pattern. Documentation is shown in `Test Details` and
   * also as a tooltip in `Statistics by Tag`. Pattern can
   * use `*`, `?` and `[]` as wildcards like --test.
   * Documentation can contain formatting like --doc.
   * ### Examples:
   *    tagDoc = mapOf("mytag" to "Example")
   *    tagDoc = mapOf("owner-*" to "Original author")
   */
  @Suppress("private")
  var tagDoc by GradleStringMapProperty(objects)

  /**
   * Add external links into `Statistics by
   * Tag`. Pattern can use `*`, `?` and `[]` as wildcards
   * like [test]. Characters matching to `*` and `?`
   * wildcards can be used in link and title with syntax
   * %N, where N is index of the match (starting from 1).
   * ### Examples:
   *    tagStatLink = listOf("mytag:http://my.domain:Title")
   *    tagStatLink = listOf("bug-*:http://url/id=%1:Issue Tracker")
   */
  @Suppress("private")
  var tagStatLink by GradleStringListProperty(objects, mutableListOf())

  /**
   * Remove keywords and their messages altogether.
   * Instructions at [http://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html#removing-keywords].
   * + `ALL` - Remove data from all keywords unconditionally.
   * + `PASSED` -Remove keyword data from passed test cases. In most
   *   cases, log files created using this option contain enough information
   *   to investigate possible failures.
   * + `FOR` - Remove all passed iterations from for loops except the last one.
   * + `WUKS` - Remove all failing keywords inside BuiltIn keyword
   *   'Wait Until Keyword Succeeds' except the last one.
   * + `NAME:<pattern>` - Remove data from all keywords matching the given pattern regardless the keyword status.
   * + `TAG:<pattern>` - Remove data from keywords with tags that match the given pattern.
   *
   * The `<pattern>` is case, space, and underscore insensitive,
   * and it supports simple patterns with * and ? as wildcards.
   */
  @Suppress("private")
  var removeKeywords by GradleStringListProperty(objects, mutableListOf())

  /**
   * Flatten keywords and their messages altogether.
   * Instructions at [http://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html#flattening-keywords].
   * + `FOR` - Flatten for loops fully.
   * + `FORITEM` - Flatten individual for loop iterations.
   * + `NAME:<pattern>` - Flatten keywords matching the given pattern.
   * + `TAG:<pattern>` - Flatten keywords with tags matching the given pattern.
   *
   * The `<pattern>` is case, space, and underscore insensitive,
   * and it supports simple patterns with * and ? as wildcards.
   */
  @Suppress("private")
  var flattenKeywords by GradleStringListProperty(objects, mutableListOf())

  /**
   * Sets the return code to zero regardless of failures
   * in test cases. Error codes are returned normally.
   */
  @Suppress("private")
  var noStatusSrc by GradleProperty(objects, Boolean::class, false)

  /**
   * Use colors on console output or not.
   * + `auto`: use colors when output not redirected (default)
   * + `on`:   always use colors
   * + `ansi`: like `on` but use ANSI colors also on Windows
   * + `off`:  disable colors altogether
   * Note that colors do not work with Jython on Windows.
   */
  @Suppress("private")
  var consoleColors by GradleProperty(objects, String::class)

  /**
   * Text file to read more arguments from. Use special
   * path `STDIN` to read contents from the standard input
   * stream. File can have both options and input files
   * or directories, one per line. Contents do not need to
   * be escaped but spaces in the beginning and end of
   * lines are removed. Empty lines and lines starting
   * with a hash character (#) are ignored.
   * ### Example file:
   *    --include regression
   *    --name Regression Tests
   *    # This is a comment line
   *    my_tests.robot
   *    path/to/test/directory/
   * ### Examples:
   *    argumentFile = "argfile.txt"
   *    argumentFile = "STDIN"
   */
  @Suppress("private")
  var argumentFiles by GradleStringListProperty(objects)

  //</editor-fold>

  override fun generateArguments(): Array<String> = Arguments().apply {
    addArgs(super.generateArguments())
    addFlagToArguments(rpa.orNull, "--rpa")
    addStringToArguments(doc.orNull, "--doc")
    addMapToArguments(metaData, "--metadata")
    addListToArguments(setTags, "--settag")
    addListToArguments(test, "--test")
    addListToArguments(task, "--task")
    addListToArguments(suite, "--suite")
    addListToArguments(include, "--include")
    addListToArguments(exclude, "--exclude")
    addListToArguments(critical, "--critical")
    addListToArguments(nonCritical, "--noncritical")
    addFileToArguments(outputDir.get().asFile, "-d")
    addOptionalFile(output, "-o")
    addFileToArguments(log, "-l")
    addFileToArguments(report, "-r")
    addOptionalFile(xUnit, "-x")
    addFlagToArguments(xUnitSkipNonCritical.orNull, "--xunitskipnoncritical")
    addFlagToArguments(timestampOutputs.orNull, "--timestampoutputs")
    addFlagToArguments(split.orNull, "--splitoutputs")
    addNonEmptyStringToArguments(logTitle.orNull, "--logtitle")
    addNonEmptyStringToArguments(reportTitle.orNull, "--reporttitle")
    addNonEmptyStringToArguments(reportBackground.orNull, "--reportbackground")
    addNonEmptyStringToArguments(logLevel.orNull, "-L")
    addStringToArguments(suiteStatLevel.orNull?.toString(), "--suitestatlevel")
    addListToArguments(tagStatInclude, "--tagstatinclude")
    addListToArguments(tagStatExclude, "--tagstatexclude")
    addMapToArguments(tagStatCombine, "--tagstatcombine")
    addMapToArguments(tagDoc, "--tagdoc")
    addListToArguments(tagStatLink, "--tagstatlink")
    addListToArguments(removeKeywords, "--removekeywords")
    addListToArguments(flattenKeywords, "--flattenkeywords")
    addFlagToArguments(noStatusSrc.orNull, "--nostatusrc")
    addStringToArguments(consoleColors.orNull, "--consolecolors")
    addListToArguments(argumentFiles, "--argumentfile")
  }.toArray()
}


open class CommonRobotConfiguration @Inject constructor(protected val objects: ObjectFactory) {

  /**
   * Sets the name of the documented library or resource.
   */
  @Suppress("private")
  val name: Property<String?> = objects.property(String::class.java)

  /**
   * Additional locations where to search for libraries and resources.
   * e.g. src/main/java/com/test/
   */
  @Suppress("private")
  var additionalPythonPaths: ConfigurableFileCollection = objects.fileCollection()

  open fun generateArguments(): Array<String> = Arguments().apply {
    addStringToArguments(name.orNull, "--name")
    val files = additionalPythonPaths.files.toList()
    addFileListToArguments(files, "--pythonpath")
  }.toArray()

  protected fun joinPaths(vararg parts: String): String = parts.joinToString(File.separator)
}
