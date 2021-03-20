package de.qualersoft.robotframework.gradleplugin

import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.gradle.testkit.runner.BuildResult
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.nio.charset.StandardCharsets
import java.nio.file.Paths

class TestdocTest : BaseRobotFrameworkFunctionalTest() {

  override fun rootFolder(): String = "testdoc"

  @Test
  @GroovyTag
  @DisplayName("When run testdoc with minimal groovy config, both files are respected.")
  fun testMinimalRunGroovy() {
    val result = setupGroovyTest("run_minimal_testdoc")
      .withArguments("testdocRun")
      .build()

    runShouldSucceed(result)
    performOutputCheck { actual ->
      assertAll(
        { actual shouldContain "Suite description for test1" },
        { actual shouldContain "Create some output test" },
        { actual shouldContain "Suite description for test2" },
        { actual shouldContain "Create some more output test" }
      )
    }
  }

  @Test
  @KotlinTag
  @DisplayName("When run testdoc with minimal kotlin config, both files are respected.")
  fun testMinimalRunKotlin() {
    val result = setupKotlinTest("run_minimal_testdoc")
      .withArguments("testdocRun")
      .build()

    runShouldSucceed(result)
    performOutputCheck { actual ->
      assertAll(
        { actual shouldContain "Suite description for test1" },
        { actual shouldContain "Create some output test" },
        { actual shouldContain "Suite description for test2" },
        { actual shouldContain "Create some more output test" }
      )
    }
  }

  @Test
  @GroovyTag
  @DisplayName("When run testdoc with 'exclude' groovy config, only one files will be respected.")
  fun testWithSetTagsRunGroovy() {
    val result = setupGroovyTest("run_testdoc_exclude")
      .withArguments("testdocRun")
      .build()

    runShouldSucceed(result)
    performOutputCheck { actual ->
      assertAll(
        { actual shouldNotContain "Suite description for test1" },
        { actual shouldNotContain "Create some output test" },
        { actual shouldNotContain "This is a simple documentation" },
        { actual shouldContain "Suite description for test2" },
        { actual shouldContain "Create some more output test" },
        { actual shouldContain "This is another simple documentation" }
      )
    }
  }

  @Test
  @KotlinTag
  @DisplayName("When run testdoc with 'exclude' kotlin config, only one file will be respected.")
  fun testWithSetTagsRunKotlin() {
    val result = setupKotlinTest("run_testdoc_exclude")
      .withArguments("testdocRun")
      .build()

    runShouldSucceed(result)
    performOutputCheck { actual ->
      assertAll(
        { actual shouldNotContain "Suite description for test1" },
        { actual shouldNotContain "Create some output test" },
        { actual shouldNotContain "This is a simple documentation" },
        { actual shouldContain "Suite description for test2" },
        { actual shouldContain "Create some more output test" },
        { actual shouldContain "This is another simple documentation" }
      )
    }
  }

  private fun runShouldSucceed(result: BuildResult) {
    result.output shouldContain "BUILD SUCCESSFUL"
  }

  private fun performOutputCheck(test: (String) -> Unit) {
    val path = Paths.get(testProjectDir.root.path, "build", "doc", "testdoc.html")
    val content = path.toFile().readText(StandardCharsets.UTF_8)
    test(content)
  }
}