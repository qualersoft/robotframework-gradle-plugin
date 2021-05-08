package de.qualersoft.robotframework.gradleplugin

import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.file.haveExtension
import io.kotest.matchers.shouldNot
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.nio.file.Paths

class RebotTest : BaseRobotFrameworkFunctionalTest() {

  override fun rootFolder(): String = "rebot/$subDir"

  private var subDir = ""

  @Test
  @GroovyTag
  @DisplayName("Run rebot with default config from groovy build script.")
  fun testDefaultRunGroovy() {
    subDir = "defaults"
    val result = setupGroovyTest("build_rebot_defaults")
      .withArguments("rebotRun")
      .build()

    runShouldSucceed(result)
    assertOutputWasCreated(
      "/build/reports/robotframework",
      listOf("log.html", "report.html", "robot-xunit-results.xml")
    )
  }

  @Test
  @KotlinTag
  @DisplayName("Run rebot with default config from kotlin build script.")
  fun testDefaultRunKotlin() {
    subDir = "defaults"
    val result = setupKotlinTest("build_rebot_defaults")
      .withArguments("rebotRun")
      .build()

    runShouldSucceed(result)
    assertOutputWasCreated(
      "/build/reports/robotframework",
      listOf("log.html", "report.html", "robot-xunit-results.xml")
    )
  }

  @Test
  @GroovyTag
  @DisplayName("Run rebot with supplied config from groovy build script.")
  fun testConfigRunGroovy() {
    subDir = "config"
    val result = setupGroovyTest("build_rebot_config")
      .withArguments("rebotRun")
      .build()

    runShouldSucceed(result)
    assertAll(
      { assertOutputWasCreated("/build/reports/robot", listOf("rfLog.html", "rfReport.html")) },
      // This time we do not expect an x-unit-file
      {
        assertAll(
          (testProjectDir.root.listFiles()?.toList() ?: emptyList()).map {
            { it shouldNot haveExtension("xml") }
          }
        )
      }
    )
  }

  @Test
  @KotlinTag
  @DisplayName("Run rebot with supplied config from kotlin build script.")
  fun testConfigRunKotlin() {
    subDir = "config"
    val result = setupKotlinTest("build_rebot_config")
      .withArguments("rebotRun")
      .build()

    runShouldSucceed(result)
    assertAll(
      { assertOutputWasCreated("/build/reports/robot", listOf("rfLog.html", "rfReport.html")) },
      // This time we do not expect an x-unit-file
      {
        assertAll(
          (testProjectDir.root.listFiles()?.toList() ?: emptyList()).map {
            { it shouldNot haveExtension("xml") }
          }
        )
      }
    )
  }

  private fun assertOutputWasCreated(outDir: String, expectedFiles: List<String>) {
    val root = Paths.get(testProjectDir.root.absolutePath, outDir)
    val files = root.toFile().listFiles()?.map { it.name } ?: listOf()
    files shouldContainAll expectedFiles
  }
}
