package de.qualersoft.robotframework.gradleplugin.configurations

import de.qualersoft.robotframework.gradleplugin.utils.Arguments
import de.qualersoft.robotframework.gradleplugin.utils.GradleDirectoryProperty
import de.qualersoft.robotframework.gradleplugin.utils.GradleFileProperty
import de.qualersoft.robotframework.gradleplugin.utils.GradleNullableProperty
import org.gradle.api.Project
import org.gradle.api.file.Directory
import java.io.File


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
class LibdocRobotConfiguration(val project: Project) : CommonRobotConfiguration(project.objects) {

  //<editor-fold desc="Properties">
  /**
   * Specifies the directory where documentation files are written. Considered
   * to be relative to the `${basedir}` of the project.
   * Default-value: `${project.buildDir}/robotframework/libdoc`
   */
  @Suppress("private")
  var outputDirectory: Directory by GradleDirectoryProperty(project,
      File(project.buildDir, joinPaths("robotframework", "libdoc")))

  /**
   * Specifies the filename of the created documentation. Considered to be
   * relative to the [outputDirectory] of the project.
   * Default-value: `libdoc.html`
   */
  @Suppress("private")
  var outputFile by GradleFileProperty(project, File("libdoc.html"))

  /**
   * Sets the version of the documented library or resource.
   */
  @Suppress("private")
  var version by GradleNullableProperty(project, String::class)

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
   * must point to a valid Python/Java source file or a resource file. For
   * example `src/main/java/com/test/ExampleLib.java`
   *
   * One may also use ant-like patterns, for example
   * `src/main/java/com/**/Lib.java`
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
    return if (file.isFile) {
      // 1. Single file specification, no patterns (try to resolve to projectDir)
      listOf(file.absolutePath)
    } else if (pattern.contains("\\") || pattern.contains("/")) {
      // 2. we have path structure (\ | /)
      when {
        file.isDirectory -> file.listFiles()?.filter { it.isFile }?.map { it.absolutePath }
        pattern.contains(Regex("[*?]")) -> {
          project.fileTree(project.projectDir).also {
            it.include(pattern)
          }.files.filter { it.isFile }.map { it.absolutePath }
        }
        else -> null
      }
    } else {
      // 3. we assume a class name
      listOf(pattern)
    } ?: throw IllegalArgumentException("The value of libraryOrResourceFile can not interpreted as path or name!")
  }

  private fun generateLibdocArgumentList(fileArgument: String, multiOutput: Boolean) = Arguments().apply {
    this.addArgs(generateArguments())
    if (multiOutput) {
      val partName = extractFileName(fileArgument)
      this.addNonEmptyStringToArguments(partName, "--name")
    } else {
      this.addNonEmptyStringToArguments(name.orNull, "--name")
    }

    this.addNonEmptyStringToArguments(version, "--version")
    this.add(fileArgument)

    val normalizedName = if (multiOutput) extractFileName(fileArgument) else outputFile.name
    this.add(joinPaths(outputDirectory.asFile.absolutePath, normalizedName))
    if (!outputDirectory.asFile.exists()) {
      outputDirectory.asFile.mkdirs()
    }
    println("Writing output to directory '${outputDirectory.asFile.absolutePath}'")
  }

  private fun extractFileName(file: String): String {
    val tmp = File(file)
    val ext = tmp.extension
    val name = tmp.nameWithoutExtension.replace("/|\\.|\\\\", "_")
        .replace(Regex("_+"), "_")
    return "$name.$ext"
  }
}