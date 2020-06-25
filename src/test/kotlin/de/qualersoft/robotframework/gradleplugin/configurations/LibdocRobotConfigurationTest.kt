package de.qualersoft.robotframework.gradleplugin.configurations

import de.qualersoft.robotframework.gradleplugin.PLUGIN_ID
import de.qualersoft.robotframework.gradleplugin.extensions.RobotFrameworkExtension
import de.qualersoft.robotframework.gradleplugin.robotframework
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File

class LibdocRobotConfigurationTest : ConfigurationTestBase() {

  private val project: Project = ProjectBuilder.builder().build().also {
    this.javaClass.getResource("/ALibraryFile.robot").openStream().use { iS ->
      var fl = File(it.projectDir, "/src/test/resources")
      fl.mkdirs()
      fl = File(fl, "ALibraryFile.robot")
      fl.createNewFile()
      fl.outputStream().use { os ->
        iS.copyTo(os)
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

    result shouldNot beNull()
    result shouldNot beEmpty()
    result.first().toArray().toList() should haveElementContains("ALibraryFile.robot")
  }

  @Test
  fun `generate with single resource file`() {
    val result = applyConfig {
      it.libraryOrResourceFile = "src/test/resources/ALibraryFile.robot"
    }.generateRunArguments()

    result shouldNot beNull()
    result shouldNot beEmpty()
    result.first().toArray().toList() should haveElementContains("ALibraryFile.robot")
  }

  @Test
  fun `generate with wildcard lib or resource file`() {
    val result = applyConfig {
      it.libraryOrResourceFile = "src/test/resources/*.robot"
    }.generateRunArguments()

    result shouldNot beNull()
    result shouldNot beEmpty()
    result.first().toArray().toList() should haveElementContains("ALibraryFile.robot")
  }

  @Test
  fun `generate with folder wildcard lib or resource file`() {
    val result = applyConfig {
      it.libraryOrResourceFile = "**/resources/*.robot"
    }.generateRunArguments()

    result shouldNot beNull()
    result shouldNot beEmpty()
    result.first().toArray().toList() should haveElementContains("ALibraryFile.robot")
  }

  @Test
  fun `When libraryOrResourceFile is some strange pattern, an exception is thrown`() {
    assertThrows<IllegalArgumentException> {
      applyConfig {
        it.libraryOrResourceFile = "//.asdf.\\"
      }.generateRunArguments()
    }
  }

  private fun applyConfig(conf: (LibdocRobotConfiguration) -> Unit): LibdocRobotConfiguration {
    rfExtension.libdoc(conf)
    return rfExtension.libdoc
  }

}