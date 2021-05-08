package de.qualersoft.robotframework.gradleplugin

import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
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
        { actual shouldContain SUITE_DESC_T1 },
        { actual shouldContain OUTPUT_T1 },
        { actual shouldContain SUITE_DESC_T2 },
        { actual shouldContain OUTPUT_T2 }
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
        { actual shouldContain SUITE_DESC_T1 },
        { actual shouldContain OUTPUT_T1 },
        { actual shouldContain SUITE_DESC_T2 },
        { actual shouldContain OUTPUT_T2 }
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
        { actual shouldNotContain SUITE_DESC_T1 },
        { actual shouldNotContain OUTPUT_T1 },
        { actual shouldNotContain DESC_T1 },
        { actual shouldContain SUITE_DESC_T2 },
        { actual shouldContain OUTPUT_T2 },
        { actual shouldContain DESC_T2 }
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
        { actual shouldNotContain SUITE_DESC_T1 },
        { actual shouldNotContain OUTPUT_T1 },
        { actual shouldNotContain DESC_T1 },
        { actual shouldContain SUITE_DESC_T2 },
        { actual shouldContain OUTPUT_T2 },
        { actual shouldContain DESC_T2 }
      )
    }
  }

  private fun performOutputCheck(test: (String) -> Unit) {
    val path = Paths.get(testProjectDir.root.path, "build", "doc", "testdoc.html")
    val content = path.toFile().readText(StandardCharsets.UTF_8)
    test(content)
  }

  companion object {
    const val SUITE_DESC_T1 = "Suite description for test1"
    const val OUTPUT_T1 = "Create some output test"
    const val DESC_T1 = "This is a simple documentation"

    const val SUITE_DESC_T2 = "Suite description for test2"
    const val OUTPUT_T2 = "Create some more output test"
    const val DESC_T2 = "This is another simple documentation"
  }
}
