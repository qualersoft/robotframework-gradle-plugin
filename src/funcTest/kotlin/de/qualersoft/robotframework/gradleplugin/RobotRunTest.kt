package de.qualersoft.robotframework.gradleplugin

import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

class RobotRunTest : BaseRobotFrameworkFunctionalTest() {

  override fun rootFolder(): String? = subFolder?.let { "run/$it" }

  var subFolder: String? = null

  @Test
  @GroovyTag
  @DisplayName("When run with minimal groovy settings, 'hello world' should be printed")
  fun testMinimalRunGroovy() {
    subFolder = "defaultrunner"
    val result = setupGroovyTest("run_minimal_test")
      .withArguments("robotRun")
      .build()

    runShouldSucceed(result)
    result.output shouldContain HI
  }

  @Test
  @KotlinTag
  @DisplayName("When run with minimal kotlin settings, 'hello world' should be printed")
  fun testMinimalRunKotlin() {
    subFolder = "defaultrunner"
    val result = setupKotlinTest("run_minimal_test")
      .withArguments("robotRun")
      .build()

    runShouldSucceed(result)
    result.output shouldContain HI
  }

  @Test
  @GroovyTag
  @DisplayName("When run with exclude config with groovy, 'goodbye' should be printed")
  fun testApplyConfigRunGroovy() {
    subFolder = "config"
    val result = setupGroovyTest("run_config")
      .withArguments("robotRun")
      .build()

    runShouldSucceed(result)
    assertAll(
      { result.output shouldContain BYE },
      { result.output shouldNotContain HI }
    )
  }

  @Test
  @KotlinTag
  @DisplayName("When run with exclude config with kotlin, 'goodbye' should be printed")
  fun testApplyConfigRunKotlin() {
    subFolder = "config"
    val result = setupKotlinTest("run_config")
      .withArguments("robotRun")
      .build()

    runShouldSucceed(result)
    assertAll(
      { result.output shouldContain BYE },
      { result.output shouldNotContain HI }
    )
  }

  companion object {
    const val HI = "Hello world"
    const val BYE = "Goodbye"
  }
}
