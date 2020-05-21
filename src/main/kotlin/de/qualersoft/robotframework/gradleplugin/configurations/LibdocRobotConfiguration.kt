package de.qualersoft.robotframework.gradleplugin.configurations

import de.qualersoft.robotframework.gradleplugin.harvester.ClassNameHarvester
import de.qualersoft.robotframework.gradleplugin.harvester.HarvestUtils
import de.qualersoft.robotframework.gradleplugin.harvester.ResourceNameHarvester
import de.qualersoft.robotframework.gradleplugin.harvester.SourceFileNameHarvester
import de.qualersoft.robotframework.gradleplugin.utils.Arguments
import de.qualersoft.robotframework.gradleplugin.utils.GradleFileNullableProperty
import de.qualersoft.robotframework.gradleplugin.utils.GradleFileProperty
import de.qualersoft.robotframework.gradleplugin.utils.GradleListProperty
import de.qualersoft.robotframework.gradleplugin.utils.GradleNullableProperty
import org.gradle.api.Project
import java.io.File
import java.io.IOException
import kotlin.collections.ArrayList


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
 *                            Considered relative to the `${basedir}` of the project, but also supports absolute paths.
 *                            Defaults to `${project.build.directory}/robotframework/libdoc`
 * + [name]                  Sets the name of the documented library or resource.
 * + [version]               Sets the version of the documented library or resource.
 * + [extraPathDirectories]  A directory to be added to the PYTHONPATH/CLASSPATH when creating documentation.
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
class LibdocRobotConfiguration(private val project: Project) : CommonRobotConfiguration(project) {

  //<editor-fold desc="Properties">
  /**
   * Specifies the directory where documentation files are written. Considered
   * to be relative to the `${basedir}` of the project.
   * Default-value: `${project.buildDir}/robotframework/libdoc`
   */
  @Suppress("private")
  var outputDirectory: File by GradleFileProperty(project,
    File(project.buildDir, joinPaths("robotframework", "libdoc")))

  /**
   * Specifies the filename of the created documentation. Considered to be
   * relative to the [outputDirectory] of the project.
   */
  @Suppress("private")
  val outputFile by GradleFileNullableProperty(project)

  /**
   * Sets the version of the documented library or resource.
   */
  @Suppress("private")
  val version by GradleNullableProperty(project, String::class)

  /**
   * Name or path of the documented library or resource file.
   *
   * Name must be in the same format as when used in Robot Framework test
   * data, for example `BuiltIn` or
   * `com.acme.FooLibrary`. When name is used, the library is
   * imported the same as when running the tests. Use
   * [extraPathDirectories] to set PYTHONPATH/CLASSPATH accordingly.
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
  val libraryOrResourceFile by GradleNullableProperty(project, String::class)

  /**
   * A directory to be added to the PYTHONPATH/CLASSPATH when creating
   * documentation.
   * e.g. src/main/java/com/test/
   */
  @Suppress("private")
  val extraPathDirectories by GradleListProperty(project, File::class)

  /**
   * The default location where extra packages will be searched. Effective if extraPathDirectories
   * attribute is not used.
   *
   * @parameter default-value="${project.basedir}/src/test/resources/robotframework/libraries"
   * @readonly
   */
  @Suppress("private")
  var defaultExtraPath by GradleFileProperty(project, File(project.projectDir,
    joinPaths("src", "test", "resources", "robotframework", "libraries")))
  //</editor-fold>

  fun generateRunArguments(): List<Array<String>> {
    val result = ArrayList<Array<String>>()

    val projectBaseDir = project.projectDir
    // Phase I - harvest the files/classes/resources, if any
    val fileArguments: List<String> = if (null != libraryOrResourceFile) {
      harvestResourceOrFileCandidates(projectBaseDir, libraryOrResourceFile!!)
    } else listOf()

    // Phase II - prepare the argument lines for the harvested files/classes/resources.

    /* with single argument line, we can use the original single entity parameters,
    souse this flag to switch. */
    val multipleOutputs = 1 < fileArguments.size

    for (fileArgument in fileArguments) {
      val generatedArguments = generateLibdocArgumentList(projectBaseDir, multipleOutputs, fileArgument)
      result.add(generatedArguments.toArray())
    }
    return result
  }

  private fun generateLibdocArgumentList(projectBaseDir: File, multipleOutputs: Boolean,
                                         fileArgument: String): Arguments = Arguments().apply {
    this.add("libdoc")
    if (multipleOutputs) {
      // Derive the name from the input.
      this.addNonEmptyStringToArguments(HarvestUtils.extractName(fileArgument), "--name")
    } else {
      // Preserve the original single-file behavior.
      this.addNonEmptyStringToArguments(name, "--name")
    }
    this.addNonEmptyStringToArguments(version, "--version")
    this.addFileListToArguments(getExtraPathDirectoriesWithDefault(), "--pythonpath")
    this.add(fileArgument)
    if (multipleOutputs) {
      // Derive the output file name id from the source and from the
      // output file given.
      // Generate a unique name.
      val normalizedArgument: String = if (HarvestUtils.isAbsolutePathFragment(fileArgument)) {
        // Cut out the project directory, so that we have shorter id
        // names.
        // TODO - perhaps later, we can preserve the directory structure
        // relative to the output directory.
        HarvestUtils.removePrefixDirectory(projectBaseDir, fileArgument)
      } else {
        fileArgument
      }
      this.add(outputDirectory.absolutePath + File.separator +
        HarvestUtils.generateIdName(normalizedArgument) +
        HarvestUtils.extractExtension(outputFile!!.name))
    } else {
      // Preserve original single-file behavior.
      if (outputFile!!.name.contains("*")) {
        // We deal with a pattern, so we need to get the name from the
        // input file.
        val tf = File(fileArgument)
        this.add(outputDirectory.absolutePath + File.separator + tf.name
          + HarvestUtils.extractExtension(outputFile!!.name))
      } else {
        // Use the output name directly.
        this.add(joinPaths(outputDirectory.absolutePath, outputFile!!.name))
      }
    }
  }

  private fun harvestResourceOrFileCandidates(rootDir: File, pattern: String): ArrayList<String> {
    val libOrResource = File(pattern)
    val fileArguments = ArrayList<String>()
    if (libOrResource.isFile) {
      // Single file specification, no patterns.
      fileArguments.add(libOrResource.absolutePath)
    } else {
      // Possible pattern, process further.
      fileArguments.addAll(processPattern(rootDir, pattern))
    } // single file or pattern
    return fileArguments
  }

  private fun processPattern(rootDir: File, pattern: String): Set<String> {
    val harv = SourceFileNameHarvester(rootDir)
    return if (HarvestUtils.hasDirectoryStructure(pattern)) {
      // Directory structure, no class resolution, harvest file names.
      harv.harvest(pattern)
    } else {
      // A) May have files, try for harvesting file names first.
      var harvested = harv.harvest(pattern)
      if (harvested.isNotEmpty()) {
        harvested
      } else {
        // B) If no files found, try harvesting classes.
        harvested = ClassNameHarvester().harvest(pattern)
        if (harvested.isNotEmpty()) {
          harvested
        } else {
          // C) If no files found, try harvesting resources.
          ResourceNameHarvester().harvest(pattern)
        } // resources
      } // classes
    } // files
  }

  private fun getExtraPathDirectoriesWithDefault(): List<File> {
    return if (extraPathDirectories.isEmpty()) {
      listOf(defaultExtraPath)
    } else {
      extraPathDirectories
    }
  }

  @Throws(IOException::class)
  fun ensureOutputDirectoryExists() {
    if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
      throw IOException("Target output directory cannot be created: " + outputDirectory.absolutePath)
    }
  }

  private fun joinPaths(vararg parts: String): String = parts.joinToString(File.separator)
}