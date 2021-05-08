package de.qualersoft.robotframework.gradleplugin.configurations

import de.qualersoft.robotframework.gradleplugin.PLUGIN_ID
import de.qualersoft.robotframework.gradleplugin.extensions.RobotFrameworkExtension
import de.qualersoft.robotframework.gradleplugin.robotframework
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.collections.shouldNotContainAnyOf
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal class RebotRobotConfigurationTest : ConfigurationTestBase() {

  @Test
  fun `Generate default run arguments`() {
    val result = applyConfig { }.generateArguments()
    result shouldNotContainAnyOf listOf(
      "--merge", "--processemptysuite", "--expandkeywords", "--starttime",
      "--endtime", "--perrobotmodifier"
    )
  }

  @Test
  fun `With merge flag`() {
    val result = applyConfig {
      it.merge.set(true)
    }.generateArguments()
    assertAll(
      { result shouldContain "--merge" },
      {
        result shouldNotContainAnyOf listOf(
          "--processemptysuite", "--expandkeywords", "--starttime",
          "--endtime", "--perrobotmodifier"
        )
      }
    )
  }

  @Test
  fun `With processEmptySuite flag`() {
    val result = applyConfig {
      it.processEmptySuite.set(true)
    }.generateArguments()
    assertAll(
      { result shouldContain "--processemptysuite" },
      {
        result shouldNotContainAnyOf listOf(
          "--merge", "--expandkeywords", "--starttime",
          "--endtime", "--perrobotmodifier"
        )
      }
    )
  }

  @Test
  fun `With single expandKeywords entry`() {
    val result = applyConfig {
      it.expandKeywords = mutableListOf("name:a")
    }.generateArguments()
    assertAll(
      { result shouldContainInOrder listOf("--expandkeywords", "name:a") },
      {
        result shouldNotContainAnyOf listOf(
          "--merge", "--processemptysuite", "--starttime",
          "--endtime", "--perrobotmodifier"
        )
      }
    )
  }

  @Test
  fun `With multiple expandKeywords entries`() {
    val result = applyConfig {
      it.expandKeywords = mutableListOf("name:a", "tag:b")
    }.generateArguments()
    assertAll(
      { result shouldContainInOrder listOf("--expandkeywords", "name:a", "--expandkeywords", "tag:b") },
      {
        result shouldNotContainAnyOf listOf(
          "--merge", "--processemptysuite", "--starttime",
          "--endtime", "--perrobotmodifier"
        )
      }
    )
  }

  @Test
  fun `With startTime flag`() {
    val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
    val date = LocalDateTime.now().format(formatter)
    val result = applyConfig {
      it.startTime.set(date)
    }.generateArguments()
    assertAll(
      { result shouldContainInOrder listOf("--starttime", date) },
      {
        result shouldNotContainAnyOf listOf(
          "--merge", "--processemptysuite", "--expandkeywords",
          "--endtime", "--perrobotmodifier"
        )
      }
    )
  }

  @Test
  fun `With endTime flag`() {
    val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
    val date = LocalDateTime.now().format(formatter)
    val result = applyConfig {
      it.endTime.set(date)
    }.generateArguments()
    assertAll(
      { result shouldContainInOrder listOf("--endtime", date) },
      {
        result shouldNotContainAnyOf listOf(
          "--merge", "--processemptysuite", "--expandkeywords",
          "--starttime", "--perrobotmodifier"
        )
      }
    )
  }

  @Test
  fun `With single perRobotModifier entry`() {
    val result = applyConfig {
      it.perRobotModifier = mutableListOf("com.example.Modifier")
    }.generateArguments()
    assertAll(
      { result shouldContainInOrder listOf("--perrobotmodifier", "com.example.Modifier") },
      {
        result shouldNotContainAnyOf listOf(
          "--merge", "--processemptysuite", "--starttime",
          "--endtime", "--expandkeywords"
        )
      }
    )
  }

  @Test
  fun `With multiple perRobotModifier entries`() {
    val result = applyConfig {
      it.perRobotModifier = mutableListOf("com.example.Modifier1", "com.example.Modifier2")
    }.generateArguments()
    assertAll(
      {
        result shouldContainInOrder listOf(
          "--perrobotmodifier", "com.example.Modifier1",
          "--perrobotmodifier", "com.example.Modifier2"
        )
      },
      {
        result shouldNotContainAnyOf listOf(
          "--merge", "--processemptysuite", "--starttime",
          "--endtime", "--expandkeywords"
        )
      }
    )
  }

  private val project: Project = ProjectBuilder.builder().build().also {
    it.pluginManager.apply(PLUGIN_ID)
  }

  private val rfExtension: RobotFrameworkExtension = project.robotframework()

  private fun applyConfig(conf: (RebotRobotConfiguration) -> Unit): RebotRobotConfiguration {
    rfExtension.rebot(conf)
    return rfExtension.rebot.get()
  }
}
