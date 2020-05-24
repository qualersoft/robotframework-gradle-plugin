package de.qualersoft.robotframework.gradleplugin

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("smoke-test")
@DisplayName("Functional Robot Framework plugin tests")
class RobotFrameworkPluginFuncTest : BaseRobotFrameworkFunctionalTest() {

  @Test
  @KotlinTag
  @DisplayName("Applying plain plugin in Kotlin should also apply the java plugin")
  fun testMinimalKotlinBuildScript() {
    val result = setupKotlinTest("build_minimal_test")
      .withArguments("tasks")
      .build()
    result.output.lines() shouldContain "Applying java plugin"
  }

  @Test
  @KotlinTag
  @DisplayName("Applying plugin in Kotlin with java plugin should not apply the java plugin")
  fun testMinimalWithJavaKotlinBuildScript() {
    val result = setupKotlinTest("build_minimal_withjava_test")
      .withArguments("tasks")
      .build()
    result.output.lines() shouldNotContain "Applying java plugin"
  }

  @Test
  @GroovyTag
  @DisplayName("Applying plain plugin in Groovy should also apply the java plugin")
  fun testMinimalGroovyBuildScript() {
    val result = setupGroovyTest("build_minimal_test")
      .withArguments("tasks")
      .build()
    result.output.lines() shouldContain "Applying java plugin"
  }

  @Test
  @GroovyTag
  @DisplayName("Applying plugin in Groovy with java plugin should not apply the java plugin")
  fun testMinimalWithJavaGroovyBuildScript() {
    val result = setupGroovyTest("build_minimal_withjava_test")
      .withArguments("tasks")
      .build()
    result.output.lines() shouldNotContain "Applying java plugin"
  }
}