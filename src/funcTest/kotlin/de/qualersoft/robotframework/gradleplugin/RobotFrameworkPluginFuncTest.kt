package de.qualersoft.robotframework.gradleplugin

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("smoke-test")
@DisplayName("Minimal functional Robot Framework plugin tests")
class RobotFrameworkPluginFuncTest : BaseRobotFrameworkFunctionalTest() {

  @Nested
  @DisplayName("Applying plain plugin should")
  inner class PlainScript {
    @Test
    @KotlinTag
    @DisplayName("apply the java plugin in Kotlin")
    fun testMinimalKotlinBuildScript() {
      val result = setupKotlinTest("build_minimal_test")
        .withArguments("tasks")
        .build()
      result.output.lines() shouldContain APPLY_JAVA
    }

    @Test
    @GroovyTag
    @DisplayName("apply the java plugin in Groovy")
    fun testMinimalGroovyBuildScript() {
      val result = setupGroovyTest("build_minimal_test")
        .withArguments("tasks")
        .build()
      result.output.lines() shouldContain APPLY_JAVA
    }

    @Test
    @GroovyTag
    @DisplayName("add the robotframework libraray dependency in Groovy")
    fun testRobotDependencyForGroovy() {
      val result = setupGroovyTest("build_minimal_test")
        .withArguments("dependencies")
        .build()
      result.output shouldContain "org.robotframework:robotframework:4.0.1"
    }

    @Test
    @KotlinTag
    @DisplayName("add the robotframework libraray dependency in Kotlin")
    fun testRobotDependencyForKotlin() {
      val result = setupKotlinTest("build_minimal_test")
        .withArguments("dependencies")
        .build()
      result.output shouldContain "org.robotframework:robotframework:4.0.1"
    }
  }

  @Test
  @GroovyTag
  @DisplayName("Applying plugin in Groovy with java plugin should not apply the java plugin")
  fun testMinimalWithJavaGroovyBuildScript() {
    val result = setupGroovyTest("build_minimal_withjava_test")
      .withArguments("tasks")
      .build()
    result.output.lines() shouldNotContain APPLY_JAVA
  }

  @Test
  @KotlinTag
  @DisplayName("Applying plugin in Kotlin with java plugin should not apply the java plugin")
  fun testMinimalWithJavaKotlinBuildScript() {
    val result = setupKotlinTest("build_minimal_withjava_test")
      .withArguments("tasks")
      .build()
    result.output.lines() shouldNotContain APPLY_JAVA
  }

  companion object {
    const val APPLY_JAVA = "Applying java plugin"
  }
}