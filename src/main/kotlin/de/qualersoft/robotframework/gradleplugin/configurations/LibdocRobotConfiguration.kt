package de.qualersoft.robotframework.gradleplugin.configurations

import de.qualersoft.robotframework.gradleplugin.utils.Arguments
import de.qualersoft.robotframework.gradleplugin.utils.GradleDirectoryProperty
import de.qualersoft.robotframework.gradleplugin.utils.GradleFileNullableProperty
import de.qualersoft.robotframework.gradleplugin.utils.GradleNullableProperty
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
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
class LibdocRobotConfiguration(project: Project) : CommonRobotConfiguration(project) {

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
   */
  @Suppress("private")
  var outputFile by GradleFileNullableProperty(project)

  /**
   * Sets the version of the documented library or resource.
   */
  @Suppress("private")
  var version by GradleNullableProperty(project, String::class)

  /**
   * Name or path of the documented library or resource file.
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
   *
   */
  @Suppress("private")
  var libraryOrResourceFile by GradleFileNullableProperty(project)

  /**
   * The default location where extra packages will be searched. Effective if extraPathDirectories
   * attribute is not used.
   *
   * @parameter default-value="${project.basedir}/src/test/resources/robotframework/libraries"
   * @readonly
   */
  @Suppress("private")
  var defaultExtraPath: ConfigurableFileCollection = project.objects.fileCollection()
    .from(File(project.projectDir, joinPaths("src", "test", "resources", "robotframework", "libraries")))
  //</editor-fold>

  fun generateRunArguments(): List<String> = ArrayList<String>().apply {
    libraryOrResourceFile?.also {
      val generatedArguments = generateLibdocArgumentList(it.path)
      addAll(generatedArguments.toArray())
    }
  }

  private fun generateLibdocArgumentList(fileArgument: String):
    Arguments = Arguments().apply {
    this.addArgs(generateArguments())
    this.addNonEmptyStringToArguments(name, "--name")
    this.addNonEmptyStringToArguments(version, "--version")
    this.addFileListToArguments(getExtraPathDirectoriesWithDefault().toList(), "--pythonpath")
    this.add(fileArgument)
    outputFile?.also {
      this.add(joinPaths(outputDirectory.asFile.absolutePath, it.name))
    }
    if (!outputDirectory.asFile.exists()) {
      outputDirectory.asFile.mkdirs()
    }
    println("Writing output to directory '${outputDirectory.asFile.absolutePath}'")
  }

  private fun getExtraPathDirectoriesWithDefault(): FileCollection {
    val path = additionalPythonPaths
    return if ((null == path) || path.isEmpty) {
      defaultExtraPath
    } else {
      path
    }
  }
}