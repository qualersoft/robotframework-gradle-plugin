package de.qualersoft.robotframework.gradleplugin.configurations

import de.qualersoft.robotframework.gradleplugin.utils.Arguments
import de.qualersoft.robotframework.gradleplugin.utils.GradleProperty
import de.qualersoft.robotframework.gradleplugin.utils.GradleStringListProperty
import org.gradle.api.Project
import java.io.File

/**
 * Configuration for rebot task
 */
open class RebotRobotConfiguration(project: Project) : BotRobotConfiguration(project) {
  // <editor-fold desc="Properties">

  /**
   * When combining results, merge outputs together instead of putting them under a new top level suite.
   * Default-value=`false`.
   */
  @Suppress("private")
  val merge by GradleProperty(objects, Boolean::class, false)

  /**
   * Processes output also if the top level suite is
   * empty. Useful e.g. with [include]/[exclude] when it
   * is not an error that there are no matches.
   */
  @Suppress("private")
  val processEmptySuite by GradleProperty(objects, Boolean::class, false)

  /**
   * Syntax: name:&lt;pattern&gt;|tag:&lt;pattern&gt;
   *
   * Matching keywords will be automatically expanded in
   * the log file. Matching against keyword name or tags
   * work using same rules as with [removeKeywords].
   *
   * Examples:
   * ```
   * expandkeywords = listOf("name:BuiltIn.Log")
   * expandkeywords = listOf("tag:expand")
   * ```
   * @since RF 3.2
   */
  @Suppress("private")
  var expandKeywords by GradleStringListProperty(objects)

  /**
   * Set execution start time. Timestamp must be given in
   * format `2007-10-01 15:12:42.268` where all separators
   * are optional (e.g. `20071001151242268` is ok too) and
   * parts from milliseconds to hours can be omitted if
   * they are zero (e.g. `2007-10-01`). This can be used
   * to override start time of a single suite or to set
   * start time for a combined suite, which would
   * otherwise be `N/A`.
   */
  @Suppress("private")
  var startTime by GradleProperty(objects, String::class)

  /**
   * Same as [startTime] but for end time. If both options
   * are used, elapsed time of the suite is calculated
   * based on them. For combined suites, it is otherwise
   * calculated by adding elapsed times of the combined
   * suites together.
   */
  @Suppress("private")
  var endTime by GradleProperty(objects, String::class)

  /**
   * Class to programmatically modify the result
   * model before creating outputs.
   */
  @Suppress("private")
  var perRobotModifier by GradleStringListProperty(objects)
  // </editor-fold>

  override fun generateArguments(): Array<String> = Arguments().apply {
    addArgs(super.generateArguments())
    addFlagToArguments(merge.orNull, "--merge")
    addFlagToArguments(processEmptySuite.orNull, "--processemptysuite")
    addListToArguments(expandKeywords, "--expandkeywords")
    addStringToArguments(startTime.orNull, "--starttime")
    addStringToArguments(endTime.orNull, "--endtime")
    addListToArguments(perRobotModifier, "--perrobotmodifier")
  }.toArray()
}