package de.qualersoft.robotframework.gradleplugin

import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class RobotRunTest : BaseRobotFrameworkFunctionalTest() {

  override fun rootFolder(): String? {
    val fa = getFolderAction
    return if (null == fa) null else fa()
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
    result.output shouldContain "Hello world"
  }
}