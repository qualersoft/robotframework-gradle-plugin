package de.qualersoft.robotframework.gradleplugin

import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Plugin tests belonging to the robot dependency configuration")
class RobotDependencyTest : BaseRobotFrameworkFunctionalTest() {

  override fun rootFolder(): String? = "robotdependency"

  @Disabled(BUG492)
  @Test
  @KotlinTag
  @DisplayName("Configuring a different version in Kotlin script")
  fun testSetDifferentVersionKotlin() {
    val result = runDependencyTaskForKotlin("rfdependency_version_test")
    result.output shouldContain expectedVersion(version = "3.1.2")
  }

  @Test
  @GroovyTag
  @DisplayName("Configuring a different version in Groovy script")
  fun testSetDifferentVersionGroovy() {
    val result = runDependencyTaskForGroovy("rfdependency_version_test")
    result.output shouldContain expectedVersion(version = "3.1.2")
  }

  @Disabled(BUG492)
  @Test
  @KotlinTag
  @DisplayName("Configuring a different group in Kotlin script")
  fun testSetDifferentGroupKotlin() {
    val result = runDependencyTaskForKotlin("rfdependency_group_test")
    result.output shouldContain expectedVersion(group = "other.robotframework")
  }

  @Test
  @GroovyTag
  @DisplayName("Configuring a different group in Groovy script")
  fun testSetDifferentGroupGroovy() {
    val result = runDependencyTaskForGroovy("rfdependency_group_test")
    result.output shouldContain expectedVersion(group = "other.robotframework")
  }

  @Disabled(BUG492)
  @Test
  @KotlinTag
  @DisplayName("Configuring a different name in Kotlin script")
  fun testSetDifferentNameKotlin() {
    val result = runDependencyTaskForKotlin("rfdependency_name_test")
    result.output shouldContain expectedVersion(name = "newrobotframework")
  }

  @Test
  @GroovyTag
  @DisplayName("Configuring a different name in Groovy script")
  fun testSetDifferentNameGroovy() {
    val result = runDependencyTaskForGroovy("rfdependency_name_test")
    result.output shouldContain expectedVersion(name = "newrobotframework")
  }

  private fun runDependencyTaskForKotlin(script: String) = setupKotlinTest(script)
    .withArguments("dependencies")
    .build()

  private fun runDependencyTaskForGroovy(script: String) = setupGroovyTest(script)
    .withArguments("dependencies")
    .build()

  fun expectedVersion(group: String = "org.robotframework", name:String = "robotframework", version: String = "3.2", ext: String? = null): String {
    // Remark: To those who missing the classifier check: The classifier is not printed to
    // the output of 'dependencies' task so may have to find another way to check it.
    // But atm it's ok to leaf it as is
    var result = "$group:$name:$version"
    ext?.also {
      // TODO findout notation for extension
      result += ""
    }
    return result
  }
}