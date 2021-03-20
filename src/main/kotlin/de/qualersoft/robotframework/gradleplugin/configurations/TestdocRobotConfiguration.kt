package de.qualersoft.robotframework.gradleplugin.configurations

import de.qualersoft.robotframework.gradleplugin.utils.Arguments
import de.qualersoft.robotframework.gradleplugin.utils.GradleProperty
import de.qualersoft.robotframework.gradleplugin.utils.GradleStringListProperty
import de.qualersoft.robotframework.gradleplugin.utils.GradleStringMapProperty
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import java.io.File
import java.nio.file.Paths
import javax.inject.Inject

class TestdocRobotConfiguration @Inject constructor(project: Project) {

  private val objects = project.objects

  /**
   * See [CommonRobotConfiguration.name]
   */
  val name: Property<String?> = objects.property(String::class.java)

  /**
   * See [BotRobotConfiguration.doc]
   */
  var doc by GradleProperty(objects, String::class)

  /**
   * See [BotRobotConfiguration.metaData]
   */
  var metaData by GradleStringMapProperty(objects)

  /**
   * See [BotRobotConfiguration.setTags]
   */
  var setTags by GradleStringListProperty(objects)

  /**
   * See [BotRobotConfiguration.test]
   */
  var test by GradleStringListProperty(objects)

  /**
   * See [BotRobotConfiguration.suite]
   */
  var suite by GradleStringListProperty(objects)

  /**
   * See [BotRobotConfiguration.include]
   */
  var include by GradleStringListProperty(objects)

  /**
   * See [BotRobotConfiguration.exclude]
   */
  var exclude by GradleStringListProperty(objects)

  /**
   * See [BotRobotConfiguration.argumentFiles]
   */
  var argumentFiles by GradleStringListProperty(objects)

  /**
   * Set the title of the generated documentation.
   * Underscores in the title are converted to spaces.
   * The default title is the name of the top level suite.
   */
  var title by GradleProperty(objects, String::class)

  /**
   * Path where to put the documentation.
   *
   * *Default*: `${buildDir}/doc`
   */
  var outputDir: DirectoryProperty = objects.directoryProperty()
    .convention(
      project.layout.buildDirectory.dir(Paths.get("doc").toString())
    )

  /**
   * Name of the documentation file.
   *
   * *Default*: `testdoc.html`
   */
  var outputFile by GradleProperty(objects, File::class, File("testdoc.html"))

  fun generateArguments(): Array<String> = Arguments().apply {
    addStringToArguments(name.orNull, "--name")
    addStringToArguments(doc.orNull, "--doc")
    addMapToArguments(metaData, "--metadata")
    addListToArguments(setTags, "--settag")
    addListToArguments(test, "--test")
    addListToArguments(suite, "--suite")
    addListToArguments(include, "--include")
    addListToArguments(exclude, "--exclude")
    addListToArguments(argumentFiles, "--argumentfiles")
    addStringToArguments(title.orNull, "--title")
  }.toArray()
}