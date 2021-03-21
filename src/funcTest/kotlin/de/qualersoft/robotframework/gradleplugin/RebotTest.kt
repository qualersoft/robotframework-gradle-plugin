package de.qualersoft.robotframework.gradleplugin

import io.kotest.matchers.collections.shouldContainAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class RebotTest: BaseRobotFrameworkFunctionalTest() {

  override fun rootFolder(): String = "rebot/$subDir"

  private var subDir = ""

  @Test
  @GroovyTag
  @DisplayName("Run rebot with default config from groovy build script.")
  fun testDefaultRunGroovy() {
    subDir = "defaults"
    val result = setupGroovyTest("build_rebot_defaults")
      .withArguments("rebot")
      .build()

    runShouldSucceed(result)
    assertOutputWasCreated("/build/reports/robotframework", listOf("log.html", "report.html", "robot-xunit-results.xml"))
  }

  @Test
  @KotlinTag
  @DisplayName("Run rebot with default config from kotlin build script.")
  fun testDefaultRunKotlin() {
    subDir = "defaults"
    val result = setupKotlinTest("build_rebot_defaults")
      .withArguments("rebot")
      .build()

    runShouldSucceed(result)
    assertOutputWasCreated("/build/reports/robotframework", listOf("log.html", "report.html", "robot-xunit-results.xml"))
  }

  private fun assertOutputWasCreated(outDir: String, expectedFiles:List<String>) {
    val root = Paths.get(testProjectDir.root.absolutePath, outDir)
    val files = root.toFile().listFiles()?.map { it.name } ?: listOf()
    files shouldContainAll expectedFiles
  }
}