package de.qualersoft.robotframework.gradleplugin.configurations

import de.qualersoft.robotframework.gradleplugin.utils.Arguments
import de.qualersoft.robotframework.gradleplugin.utils.GradleProperty
import org.gradle.api.Project
import javax.inject.Inject

class TidyRobotConfiguration @Inject constructor(project: Project) {
  private val objects = project.objects

  /**
   * Tidy given file(s) so that original file(s) are overwritten.
   * When this option is used, it is possible to give multiple
   * input files.
   *
   * *Default-Value:* `false`
   */
  var inplace by GradleProperty(objects, Boolean::class, false)

  /**
   * Process given directory recursively. Files in the directory
   * are processed in-place similarly as when [inplace] option
   * is used. Does not process referenced resource files.
   *
   * *Default-Value:* `false`
   */
  var recursive by GradleProperty(objects, Boolean::class, false)

  /**
   * Use pipe ('|') as a column separator in the plain text format.
   *
   * *Default-Value:* `false`
   */
  var usepipes by GradleProperty(objects, Boolean::class, false)

  /**
   * Line separator to use in outputs.
   * * **`native`**:  use operating system's native line separators
   * * **`windows`**: use Windows line separators (CRLF)
   * * **`unix`**:    use Unix line separators (LF)
   *
   * *Default-Value:* `native`
   */
  var lineseparator by GradleProperty(objects, String::class, "native")

  /**
   * The number of spaces between cells in the plain text format.
   *
   * *Default-Value:* `4`
   */
  var spacecount by GradleProperty(objects, Int::class, DEFAULT_SPACES)

  fun generateArguments() = Arguments().apply {
    addFlagToArguments(inplace.orNull, "--inplace")
    addFlagToArguments(recursive.orNull, "--recursive")
    addFlagToArguments(usepipes.orNull, "--usepipes")
    addStringToArguments(lineseparator.get(), "--lineseparator")
    addStringToArguments(spacecount.get().toString(), "--spacecount")
  }.toArray()

  private companion object {
    const val DEFAULT_SPACES = 4
  }
}