package de.qualersoft.robotframework.gradleplugin.configurations

import de.qualersoft.robotframework.gradleplugin.PLUGIN_ID
import de.qualersoft.robotframework.gradleplugin.extensions.RobotFrameworkExtension
import de.qualersoft.robotframework.gradleplugin.robotframework
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.haveSize
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.containIgnoringCase
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail
import java.io.File

class LibdocRobotConfigurationTest : ConfigurationTestBase() {

  private val project: Project = ProjectBuilder.builder().build().also {
    listOf("ALibraryFile.robot", "AResourceFile.resource").forEach { res ->
      this.javaClass.getResource("/$res").openStream().use { iS ->
        var fl = File(it.projectDir, "/src/test/resources")
        fl.mkdirs()
        fl = File(fl, res)
        fl.createNewFile()
        fl.outputStream().use { os ->
          iS.copyTo(os)
        }
      }
    }
    it.pluginManager.apply(PLUGIN_ID)
  }
  private val rfExtension: RobotFrameworkExtension = project.robotframework()

  @Test
  fun `generating default run arguments`() {
    val result = applyConfig { }.generateRunArguments()
    result.size shouldBe 0
  }

  @Test
  fun `generate with single relative resource file`() {
    val result = applyConfig {
      it.libraryOrResourceFile = "./src/test/resources/ALibraryFile.robot"
    }.generateRunArguments()

    assertAll(
        { result shouldNot beNull() },
        { result shouldNot beEmpty() },
        { result.first().toArray().toList() should haveElementContains("ALibraryFile.robot") }
    )
  }

  @Test
  fun `generate with single resource file`() {
    val result = applyConfig {
      it.libraryOrResourceFile = "src/test/resources/ALibraryFile.robot"
    }.generateRunArguments()

    assertAll(
        { result shouldNot beNull() },
        { result shouldNot beEmpty() },
        { result.first().toArray().toList() should haveElementContains("ALibraryFile.robot") }
    )
  }

  @Test
  fun `generate with wildcard for lib or resource file`() {
    val result = applyConfig {
      it.libraryOrResourceFile = "src/test/resources/*.robot"
    }.generateRunArguments()

    assertAll(
        { result shouldNot beNull() },
        { result shouldNot beEmpty() },
        { result.first().toArray().toList() should haveElementContains("ALibraryFile.robot") }
    )
  }

  @Test
  fun `generate with folder wildcard for lib or resource file`() {
    val result = applyConfig {
      it.libraryOrResourceFile = "**/resources/*.robot"
    }.generateRunArguments()

    assertAll(
      { result shouldNot beNull() },
      { result shouldNot beEmpty() },
      { result.first().toArray().toList() should haveElementContains("ALibraryFile.robot") }
    )
  }

  @Test
  fun `generate with folder and pattern for lib or resource file`() {
    val result = applyConfig {
      it.libraryOrResourceFile = "src/test/**"
    }.generateRunArguments()

    assertAll(
      { result shouldNot beNull() },
      { result shouldNot beEmpty() },
      { result.first().toArray().toList() should haveElementContains("ALibraryFile.robot") },
      { result[1].toArray().toList() should haveElementContains("AResourceFile.resource") }
    )
  }

  @Test
  fun `generate with empty folder for path lib or resource file`() {
    val path = "src/test/empty/"
    val emptyDir = File(project.projectDir.absoluteFile, path)
    if(!emptyDir.mkdirs()) {
      fail("Unable to create directories $emptyDir!")
    }

    val result = applyConfig {
      it.libraryOrResourceFile = path
    }.generateRunArguments()

    assertAll(
      { result shouldNot beNull() },
      { result should beEmpty() }
    )
  }

  @Test
  fun `generate with with nonexisting folder for path lib or resource file should throw exception`() {
    val path = "src/test/notexisting/"

    val ex = assertThrows<IllegalArgumentException> {
      applyConfig {
        it.libraryOrResourceFile = path
      }.generateRunArguments()
    }
    ex.message should containIgnoringCase(path)
  }

  @Test
  fun `generate with class name for library or resource file`() {
    val result = applyConfig {
      it.libraryOrResourceFile = "de.qualersoft.robotframework.gradleplugin.tasks.LibdocTask"
    }.generateRunArguments()
    assertAll(
      { result shouldNot beNull() },
      { result shouldNot beEmpty() },
      { result.first().toArray().toList() should haveElementContains("")}
    )
  }

  @Test
  fun `When libraryOrResourceFile is some strange pattern, an exception is thrown`() {
    assertThrows<IllegalArgumentException> {
      applyConfig {
        it.libraryOrResourceFile = "//.asdf.\\"
      }.generateRunArguments()
    }
  }

  @Test
  fun `When libraryOrResourceFile has a pattern that matches multiply files, then a list of argument list is generated`() {
    val result = applyConfig {
      it.libraryOrResourceFile = "**/test/resources/A*File.*"
    }.generateRunArguments()

    assertAll(
        { result shouldNot beNull() },
        { result shouldNot beEmpty() },
        { result should haveSize(2) },
        { result[0].toArray().toList() should haveElementContains("ALibraryFile.robot") },
        { result[1].toArray().toList() should haveElementContains("AResourceFile.resource") }
    )
  }

  @Test
  fun `When given a directory for libraryOrResourceFile, then all files in it will be taken`() {
    val result = applyConfig {
      it.libraryOrResourceFile = "./src/test/resources"
    }.generateRunArguments()

    assertAll(
        { result shouldNot beNull() },
        { result shouldNot beEmpty() },
        { result should haveSize(2) },
        { result[0].toArray().toList() should haveElementContains("ALibraryFile.robot") },
        { result[1].toArray().toList() should haveElementContains("AResourceFile.resource") }
    )
  }

  private fun applyConfig(conf: (LibdocRobotConfiguration) -> Unit): LibdocRobotConfiguration {
    rfExtension.libdoc(conf)
    return rfExtension.libdoc.get()
  }

}