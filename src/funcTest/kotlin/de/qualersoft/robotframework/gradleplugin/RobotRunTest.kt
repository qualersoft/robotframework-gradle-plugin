package de.qualersoft.robotframework.gradleplugin

import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

class RobotRunTest : BaseRobotFrameworkFunctionalTest() {

  override fun rootFolder(): String? {
    val fa = getFolderAction
    return if (null == fa) null else "run/" + fa()
  }

  var getFolderAction: (() -> String?)? = null

  @Test
  @GroovyTag
  @DisplayName("When run with minimal groovy settings, 'hello world' should be printed")
  fun testMinimalRunGroovy() {
    getFolderAction = { "defaultrunner" }
    val result = setupGroovyTest("run_minimal_test")
      .withArguments("robotRun")
      .build()

    runShouldSucceed(result)
    result.output shouldContain "Hello world"
  }

  @Test
  @KotlinTag
  @DisplayName("When run with minimal kotlin settings, 'hello world' should be printed")
  fun testMinimalRunKotlin() {
    getFolderAction = { "defaultrunner" }
    val result = setupKotlinTest("run_minimal_test")
      .withArguments("robotRun")
      .build()

    runShouldSucceed(result)
    result.output shouldContain "Hello world"
  }

  @Test
  @GroovyTag
  @DisplayName("When run with exclude config with groovy, 'goodbye' should be printed")
  fun testApplyConfigRunGroovy() {
    getFolderAction = { "config" }
    val result = setupGroovyTest("run_config")
      .withArguments("robotRun")
      .build()

    runShouldSucceed(result)
    assertAll(
      { result.output shouldContain "Goodbye" },
      { result.output shouldNotContain "Hello world" }
    )
  }

  @Test
  @KotlinTag
  @DisplayName("When run with exclude config with kotlin, 'goodbye' should be printed")
  fun testApplyConfigRunKotlin() {
    getFolderAction = { "config" }
    val result = setupKotlinTest("run_config")
      .withArguments("robotRun")
      .build()

    runShouldSucceed(result)
    assertAll(
      { result.output shouldContain "Goodbye" },
      { result.output shouldNotContain "Hello world" }
    )
  }
}