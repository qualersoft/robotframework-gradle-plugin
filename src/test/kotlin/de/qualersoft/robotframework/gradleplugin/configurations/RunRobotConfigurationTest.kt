package de.qualersoft.robotframework.gradleplugin.configurations

import de.qualersoft.robotframework.gradleplugin.PLUGIN_ID
import de.qualersoft.robotframework.gradleplugin.extensions.RobotFrameworkExtension
import de.qualersoft.robotframework.gradleplugin.robotframework
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.contain
import io.kotest.matchers.collections.containAll
import io.kotest.matchers.collections.containsInOrder
import io.kotest.matchers.collections.haveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.io.File

internal class RunRobotConfigurationTest : ConfigurationTestBase() {

  private val project: Project = ProjectBuilder.builder().build().also {
    it.pluginManager.apply(PLUGIN_ID)
  }

  private val rf: RobotFrameworkExtension = project.robotframework()

  @Test
  fun settingTheSuiteStatLevelOfBotRobotConfiguration() {
    val result = applyConfig {
      it.suiteStatLevel.set(5)
    }.generateArguments().toList()

    assertAll(
      { result should containsInOrder(listOf("--suitestatlevel", "5")) }
    )
  }

  @Test
  fun `generate default run arguments`() {
    val result = applyConfig { }.generateArguments().toList()
    val expected = createDefaultsWithoutExtensionParam() + listOf("-F", "robot")
    assertAll(
      { result should haveSize(expected.size) },
      { result should containAll(expected) }
    )
  }

  @Test
  fun `add an extension to default should override default entry`() {
    val result = applyConfig {
      it.extension.add("newRobot")
    }.generateArguments().toList()
    val expected = createDefaultsWithoutExtensionParam() + listOf("-F", "newRobot")
    assertAll(
      { result should haveSize(expected.size) },
      { result should containAll(expected) },
      { result shouldNot contain("robot") }
    )
  }

  @Test
  fun `add two extensions should result in two entries`() {
    val result = applyConfig {
      it.extension.add("newRobot1")
      it.extension.add("newRobot2")
    }.generateArguments().toList()
    val expected = createDefaultsWithoutExtensionParam() + listOf("-F", "newRobot1", "-F", "newRobot2")
    assertAll(
      { result should haveSize(expected.size) },
      { result should containAll(expected) },
      { result shouldNot contain("robot") }
    )
  }

  @Test
  fun `empty the extension should also remove default`() {
    val result = applyConfig {
      it.extension.empty()
    }.generateArguments().toList()
    val expected = createDefaultsWithoutExtensionParam()
    assertAll(
      { result should haveSize(expected.size) },
      { result should containAll(expected) },
      { result shouldNot contain("robot") }
    )
  }

  @Test
  fun addSingleVariable() {
    val result = applyConfig {
      it.variables.put("MyVar", "42")
    }.generateArguments().toList()

    assertAll(
      { result shouldNot beEmpty() },
      { result should containsInOrder(listOf("-v", "MyVar:42")) }
    )
  }

  @Test
  fun addMultibleVariables() {
    val result = applyConfig {
      it.variables.putAll(mapOf("MyVar1" to "42", "MyVar2" to "0815"))
    }.generateArguments().toList()

    assertAll(
      { result shouldNot beEmpty() },
      { result should containsInOrder(listOf("-v", "MyVar1:42")) },
      { result should containsInOrder(listOf("-v", "MyVar2:0815")) }
    )
  }

  @Test
  fun addVariableFiles() {
    val result = applyConfig {
      it.variableFiles.add("./settings.property")
    }.generateArguments().toList()
    val expected = createDefaults() + listOf("-V", "./settings.property")
    assertAll(
      { result should haveSize(expected.size) },
      { result should containAll(expected) }
    )
  }

  @Test
  fun addDebugFile() {
    val file = File("C:/temp")
    val result = applyConfig {
      it.debugFile.fileValue(file)
    }.generateArguments().toList()
    val expected = createDefaults() + listOf("-b", file.absolutePath)
    assertAll(
      { result should haveSize(expected.size) },
      { result should containAll(expected) }
    )
  }

  @Test
  fun `set maxerrorlines to negative will generate NONE entry`() {
    val result = applyConfig {
      it.maxErrorLines.set(-1)
    }.generateArguments().toList()
    val expected = listOf(
      "-d", File(project.buildDir, "\\reports\\robotframework").absolutePath,
      "-l", "log.html", "-r", "report.html", "-x", "robot-xunit-results.xml", "-F", "robot",
      "--randomize", "none", "--console", "verbose", "-W", "78", "-K", "auto", "--maxerrorlines", "none"
    )
    assertAll(
      { result should haveSize(expected.size) },
      { result should containAll(expected) },
      { result shouldNot contain("40") }
    )
  }

  @Test
  fun addListener() {
    val result = applyConfig {
      it.listener.add("aListener")
    }.generateArguments().toList()
    val expected = createDefaults() + listOf("--listener", "aListener")
    assertAll(
      { result should haveSize(expected.size) },
      { result should containAll(expected) }
    )
  }

  @Test
  fun enableDryrunMode() {
    val result = applyConfig {
      it.dryrun.set(true)
    }.generateArguments().toList()
    val expected = createDefaults() + listOf("--dryrun")
    assertAll(
      { result should haveSize(expected.size) },
      { result should containAll(expected) }
    )
  }

  @Test
  fun enableExitOnFailureMode() {
    val result = applyConfig {
      it.exitOnFailure.set(true)
    }.generateArguments().toList()
    val expected = createDefaults() + listOf("-X")
    assertAll(
      { result should haveSize(expected.size) },
      { result should containAll(expected) }
    )
  }

  @Test
  fun enableExitOnErrorMode() {
    val result = applyConfig {
      it.exitOnError.set(true)
    }.generateArguments().toList()
    val expected = createDefaults() + listOf("--exitonerror")
    assertAll(
      { result should haveSize(expected.size) },
      { result should containAll(expected) }
    )
  }

  @Test
  fun enableSkipTearDownOnExitMode() {
    val result = applyConfig {
      it.skipTearDownOnExit.set(true)
    }.generateArguments().toList()
    val expected = createDefaults() + listOf("--skipteardownonexit")
    assertAll(
      { result should haveSize(expected.size) },
      { result should containAll(expected) }
    )
  }

  @Test
  fun changeRandomizeMode() {
    val result = applyConfig {
      it.randomize.set("tests:1234")
    }.generateArguments().toList()
    val expected = listOf(
      "-d", File(project.buildDir, "\\reports\\robotframework").absolutePath,
      "-l", "log.html", "-r", "report.html", "-x", "robot-xunit-results.xml", "-F", "robot",
      "--randomize", "tests:1234", "--console", "verbose", "-W", "78", "-K", "auto", "--maxerrorlines", "40"
    )
    assertAll(
      { result should haveSize(expected.size) },
      { result should containAll(expected) },
      { result shouldNot contain("none") }
    )
  }

  @Test
  fun addPreRunModifier() {
    val result = applyConfig {
      it.preRunModifier.add("preRun")
    }.generateArguments().toList()
    val expected = createDefaults() + listOf("--prerunmodifier", "preRun")
    assertAll(
      { result should haveSize(expected.size) },
      { result should containAll(expected) }
    )
  }

  @Test
  fun addPreRebotModifier() {
    val result = applyConfig {
      it.preRebotModifier.add("preRebot")
    }.generateArguments().toList()
    val expected = createDefaults() + listOf("--prerebotmodifier", "preRebot")
    assertAll(
      { result should haveSize(expected.size) },
      { result should containAll(expected) }
    )
  }

  @Test
  fun changeConsoleMode() {
    val result = applyConfig {
      it.console.set("none")
    }.generateArguments().toList()
    val expected = listOf(
      "-d", File(project.buildDir, "\\reports\\robotframework").absolutePath,
      "-l", "log.html", "-r", "report.html", "-x", "robot-xunit-results.xml", "-F", "robot",
      "--randomize", "none", "--console", "none", "-W", "78", "-K", "auto", "--maxerrorlines", "40"
    )
    assertAll(
      { result should haveSize(expected.size) },
      { result should containAll(expected) },
      { result shouldNot contain("verbose") }
    )
  }

  @Test
  fun enableDottedMode() {
    val result = applyConfig {
      it.dotted.set(true)
    }.generateArguments().toList()
    val expected = createDefaults() + listOf("-.")
    assertAll(
      { result should haveSize(expected.size) },
      { result should containAll(expected) }
    )
  }

  @Test
  fun enableQuiteMode() {
    val result = applyConfig {
      it.quite.set(true)
    }.generateArguments().toList()
    val expected = createDefaults() + listOf("--quite")
    assertAll(
      { result should haveSize(expected.size) },
      { result should containAll(expected) }
    )
  }

  @Test
  fun changeConsoleWidth() {
    val result = applyConfig {
      it.consoleWidth.set(10)
    }.generateArguments().toList()
    val expected = listOf(
      "-d", File(project.buildDir, "\\reports\\robotframework").absolutePath,
      "-l", "log.html", "-r", "report.html", "-x", "robot-xunit-results.xml", "-F", "robot",
      "--randomize", "none", "--console", "verbose", "-W", "10", "-K", "auto", "--maxerrorlines", "40"
    )
    assertAll(
      { result should haveSize(expected.size) },
      { result should containAll(expected) },
      { result shouldNot contain("78") }
    )
  }

  @Test
  fun changeConsoleMarkers() {
    val result = applyConfig {
      it.consoleMarkers.set("on")
    }.generateArguments().toList()
    val expected = listOf(
      "-d", File(project.buildDir, "\\reports\\robotframework").absolutePath,
      "-l", "log.html", "-r", "report.html", "-x", "robot-xunit-results.xml", "-F", "robot",
      "--randomize", "none", "--console", "verbose", "-W", "78", "-K", "on", "--maxerrorlines", "40"
    )
    assertAll(
      { result should haveSize(expected.size) },
      { result should containAll(expected) },
      { result shouldNot contain("auto") }
    )
  }

  private fun applyConfig(conf: (RunRobotConfiguration) -> Unit): RunRobotConfiguration {
    rf.robot(conf)
    return rf.robot.get()
  }

  private fun createDefaultsWithoutExtensionParam() = listOf(
    "-d", File(project.buildDir, "\\reports\\robotframework").absolutePath,
    "-l", "log.html", "-r", "report.html", "-x", "robot-xunit-results.xml", "--maxerrorlines", "40",
    "--randomize", "none", "--console", "verbose", "-W", "78", "-K", "auto"
  )

  private fun createDefaults() = listOf(
    "-d", File(project.buildDir, "\\reports\\robotframework").absolutePath, "-F", "robot",
    "-l", "log.html", "-r", "report.html", "-x", "robot-xunit-results.xml", "--maxerrorlines", "40",
    "--randomize", "none", "--console", "verbose", "-W", "78", "-K", "auto"
  )
}