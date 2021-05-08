package de.qualersoft.robotframework.gradleplugin

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets
import java.nio.file.Paths
import kotlin.test.assertEquals

class TidyTest : BaseRobotFrameworkFunctionalTest() {

  override fun rootFolder(): String = "tidy/$subDir"

  private var subDir = ""

  @Test
  @GroovyTag
  @DisplayName("Run tidy on single file with groovy config.")
  fun testSingleFileRunGroovy() {
    subDir = "singleFile"
    val result = setupGroovyTest("run_tidy_singleFile")
      .withArguments("tidyRun")
      .build()

    runShouldSucceed(result)
    performOutputCheck("cleaned.robot", "src/test/robot/single2Tidy.robot.clean")
  }

  @Test
  @KotlinTag
  @DisplayName("Run tidy on single file with kotlin config.")
  fun testSingleFileRunKotlin() {
    subDir = "singleFile"
    val result = setupKotlinTest("run_tidy_singleFile")
      .withArguments("tidyRun")
      .build()

    runShouldSucceed(result)
    performOutputCheck("cleaned.robot", "src/test/robot/single2Tidy.robot.clean")
  }

  @Test
  @GroovyTag
  @DisplayName("Run tidy with 'usePipe' and 'inplace' flag with groovy config.")
  fun testPipeAndInplaceRunGroovy() {
    subDir = "pipes"
    val result = setupGroovyTest("run_tidy_withPipes")
      .withArguments("tidyRun")
      .build()

    runShouldSucceed(result)
    val base = "src/test/robot/pipeTidy.robot"
    performOutputCheck(base, "$base.clean")
  }

  @Test
  @KotlinTag
  @DisplayName("Run tidy with 'usePipe' and 'inplace' flag with kotlin config.")
  fun testPipeAndInplaceRunKotlin() {
    subDir = "pipes"
    val result = setupKotlinTest("run_tidy_withPipes")
      .withArguments("tidyRun")
      .build()

    runShouldSucceed(result)
    val base = "src/test/robot/pipeTidy.robot"
    performOutputCheck(base, "$base.clean")
  }

  private fun performOutputCheck(src: String, expected: String) {
    val root = testProjectDir.root
    // Remark: because of operating-system/environment-setting we have to normalize new-line separator
    // but that does not mater to the tests purposes
    val cleanedContent = Paths.get(root.absolutePath, src).toFile()
      .readText(StandardCharsets.UTF_8).replace("\r\n", "\n")
    val expContent = Paths.get(root.absolutePath, expected).toFile()
      .readText(StandardCharsets.UTF_8).replace("\r\n", "\n")
    assertEquals(expContent, cleanedContent)
  }
}
