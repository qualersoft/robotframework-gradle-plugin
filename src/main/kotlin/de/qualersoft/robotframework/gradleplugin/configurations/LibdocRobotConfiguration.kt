package de.qualersoft.robotframework.gradleplugin.configurations

import de.qualersoft.robotframework.gradleplugin.utils.Arguments
import de.qualersoft.robotframework.gradleplugin.utils.GradleDirectoryProperty
import de.qualersoft.robotframework.gradleplugin.utils.GradleProperty
import org.gradle.api.Project
import java.io.File
import javax.inject.Inject


/**
 * Class that stores `libdoc` configuration and maps it to the command line arguments.
 *
 * # Library documentation configuration.
 * ## Required settings:
 * + [outputFile]              The name for the output file. Documentation output
 *                             format is deduced from the file extension.
 *                             We also support patterns like `*.html`, which indicates
 *                             to derive the output name from the original name.
 * + [libraryOrResourceFile]   Name or path of the documented library or resource file.
 *                             Supports ant-like pattern format to match multiple inputs,
 *                             such as `src/java/**/*.java`
 *                             Name must be in the same format as when used in Robot Framework
 *                             test data, for example `BuiltIn` or
 *                             `com.acme.FooLibrary`. When name is used, the library is
 *                             imported the same as when running the tests.
 *                             Use extraPathDirectories to set PYTHONPATH/CLASSPATH accordingly.
 *
 *                              Paths are considered relative to the location of build script
 *                              and must point to a valid Python/Java
 *                              source file or a resource file.
 *                              For example `src/main/python/test/ExampleLib.py`
 *
 *                              Note that you should preferably import java classes
 *                              by classname, not path. Dynamic libraries will not
 *                              be compiled correctly with path.
 *
 * ## Optional settings:
 * + [outputDirectory]       Specifies the directory where documentation files are written.
 *                           Considered relative to the `${basedir}` of the project, but also supports absolute paths.
 *                           Defaults to `${project.build.directory}/robotframework/libdoc`
 * + [name]                  Sets the name of the documented library or resource.
 * + [version]               Sets the version of the documented library or resource.
 * + [additionalPythonPaths] Additional locations where to search for libraries
and resources.
 * E.g.: `src/main/java/com/test/`
 *
 *
 * Example 1:
 *
 *    <libdoc>
 *      <outputFile>MyLib.html</outputFile>
 *      <libraryOrResourceFile>com.mylib.MyLib</libraryOrResourceFile>
 *    </libdoc>
 *
 * Example 2:
 *
 *    <libdoc>
 *      <outputFile>*.html</outputFile>
 *      <libraryOrResourceFile>src/java/**/*Lib.java</libraryOrResourceFile>
 *    </libdoc>
 *
 * Example 3:
 *
 *    <libdoc>
 *      <outputFile>*.html</outputFile>
 *      <libraryOrResourceFile>com.**.*Lib</libraryOrResourceFile>
 *    </libdoc>
 */
class LibdocRobotConfiguration @Inject constructor(val project: Project) : CommonRobotConfiguration(project.objects) {

  //<editor-fold desc="Properties">
  /**
   * Specifies the directory where documentation files are written.
   * Default-value: `${project.buildDir}/robotdoc/libdoc`
   */
  @Suppress("private")
  var outputDirectory by GradleDirectoryProperty(
    objects,
    project.layout.buildDirectory.dir(joinPaths("robotdoc", "libdoc"))
  )

  /**
   * Specifies the filename of the created documentation. Considered to be
   * relative to the [outputDirectory] of the project.
   * Default-value: `libdoc.html`
   */
  @Suppress("private")
  var outputFile = objects.fileProperty()
    .convention(outputDirectory.file("libdoc.html"))

  /**
   * Sets the version of the documented library or resource.
   * Default-value: project.version
   */
  @Suppress("private")
  var version by GradleProperty(objects, String::class, project.version.toString())

  /**
   * Name of the library or path to the resource file.
   *
   * Name must be in the same format as when used in Robot Framework test
   * data, for example `BuiltIn` or
   * `com.acme.FooLibrary`. When name is used, the library is
   * imported the same as when running the tests. Use
   * [additionalPythonPaths] to set PYTHONPATH/CLASSPATH accordingly.
   *
   * Paths are considered relative to the location of `build.gradle` and
   * must point to a valid Python/Java source file or a resource file.
   *
   * Examples
   * * `src/main/java/com/test/ExampleLib.java`
   * * `${buildDir}/libs/ExampleLib.jar`
   *
   * One may also use ant-like patterns, for example
   * `src/main/java/com/**/Lib.java`
   *
   * TODO This is input property of the task
   */
  @Suppress("private")
  var libraryOrResourceFile: String? = null
  //</editor-fold>

  @Throws(IllegalArgumentException::class)
  fun generateRunArguments(): List<Arguments> = libraryOrResourceFile?.let { libOrResFile ->
    val srcFiles = harvestResourceOrFileCandidates(libOrResFile)
    val multiOutput = 1 < srcFiles.size
    return srcFiles.map {
      generateLibdocArgumentList(it, multiOutput)
    }
  } ?: emptyList()

  @Throws(IllegalArgumentException::class)
  private fun harvestResourceOrFileCandidates(pattern: String): List<String> {
    val file = project.projectDir.resolve(pattern).normalize()
    return when {
      // 1. Single file specification, no patterns (try to resolve to projectDir)
      (file.isFile) -> listOf(file.absolutePath)

      // 2. we have path structure (\ | /)
      (pattern.contains("\\")
          || pattern.contains("/")) -> harvestPath(pattern, file)

      // 3. we assume a class name
      else -> listOf(pattern)
    } ?: throw IllegalArgumentException("The value of libraryOrResourceFile can not interpreted as path or name!")
  }

  private fun harvestPath(pattern: String, file: File) = when {
    file.isDirectory -> file.listFiles()?.let { files -> files.filter { it.isFile }.map { it.absolutePath } }

    pattern.contains(Regex("[*?]")) -> {
      project.fileTree(project.projectDir).also {
        it.include(pattern)
      }.files.flatMap { f ->
        // here we have a directory so null can not be returned
        if (f.isDirectory) {
          f.listFiles()!!.filter { it.isFile }.toList()
        }
        // f is a file
        else {
          listOf(f)
        }
      }.map { it.absolutePath }
    }

    else -> null
  }

  private fun generateLibdocArgumentList(fileArgument: String, multiOutput: Boolean) = Arguments().apply {
    this.addArgs(generateArguments())
    if (multiOutput) {
      val partName = extractFileName(fileArgument)
      this.addNonEmptyStringToArguments(partName, "--name")
    } else {
      this.addNonEmptyStringToArguments(name.orNull, "--name")
    }

    this.addNonEmptyStringToArguments(version.orNull, "--version")
    this.add(fileArgument)

    val normalizedName = if (multiOutput) extractFileName(fileArgument) else outputFile.asFile.get().name
    this.add(joinPaths(outputDirectory.asFile.get().absolutePath, normalizedName))
    if (!outputDirectory.asFile.get().exists()) {
      outputDirectory.asFile.get().mkdirs()
    }
  }

  private fun extractFileName(file: String): String {
    val tmp = File(file)
    val ext = tmp.extension
    val name = tmp.nameWithoutExtension.replace("/|\\.|\\\\", "_")
      .replace(Regex("_+"), "_")
    return "$name.$ext"
  }
}