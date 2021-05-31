package de.qualersoft.robotframework.gradleplugin

import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.sequences.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.contain
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.io.File
import java.nio.charset.StandardCharsets

open class LibdocRunTest : BaseRobotFrameworkFunctionalTest() {

  override fun rootFolder(): String? {
    val fa = getFolderAction
    return if (null == fa) null else fa()
  }

  var getFolderAction: (() -> String?)? = null

  @Test
  @KotlinTag
  @DisplayName("When run libdoc for robot-file from kotlin script, a documentation should be generated.")
  fun testGenerateDocForRobotWithKotlin() {
    getFolderAction = { "libdoc" }
    val result = setupKotlinTest("run_minimal_libdoc_robot_test")
      .withArguments("libdocRun")
      .build()

    runShouldSucceed(result)
    checkForHtmlDoc(::checkRobotLib)
  }

  @Test
  @GroovyTag
  @DisplayName("When run libdoc for robot-file from groovy script, a documentation should be generated.")
  fun testGenerateDocForRobotWithGroovy() {
    getFolderAction = { "libdoc" }
    val result = setupGroovyTest("run_minimal_libdoc_robot_test")
      .withArguments("libdocRun")
      .build()

    runShouldSucceed(result)
    checkForHtmlDoc(::checkRobotLib)
  }

  private fun checkRobotLib(file: File) {
    val content = file.readText(StandardCharsets.UTF_8)
    assertAll(
      { content should contain("Suite description") },
      { content should contain("Write stuff to the output") }
    )
  }

  @Test
  @KotlinTag
  @DisplayName("When run libdoc for java-file from kotlin script, a documentation should be generated.")
  fun testGenerateDocForJavaClassWithKotlin() {
    getFolderAction = { "libdoc" }
    val result = setupKotlinTest("run_libdoc_plain_java_test")
      .withArguments("libdocRun")
      .build()

    runShouldSucceed(result)
    checkForHtmlDoc(::checkJavaClass)
  }

  @Test
  @GroovyTag
  @DisplayName("When run libdoc for java-file from groovy script, a documentation should be generated.")
  fun testGenerateDocForJavaClassWithGroovy() {
    getFolderAction = { "libdoc" }
    val result = setupGroovyTest("run_libdoc_plain_java_test")
      .withArguments("libdocRun")
      .build()

    runShouldSucceed(result)
    checkForHtmlDoc(::checkJavaClass)
  }

  private fun checkJavaClass(file: File) {
    val content = file.readText(StandardCharsets.UTF_8)
    assertAll(
      { content should contain("Get Keyword Documentation") },
      { content should contain("This is a dummy keyword") }
    )
  }

  @Test
  @KotlinTag
  @DisplayName("When run libdoc with java-lib from kotlin script, a documentation should be generated.")
  fun testLibdocForJavaLibWithKotlin() {
    getFolderAction = { "libdoc" }
    val result = setupKotlinTest("run_libdoc_java_lib_test")
      .withArguments("assemble", "libdocRun")
      .build()

    runShouldSucceed(result)
    checkForHtmlDoc(::checkJavaLib)
  }

  @Test
  @GroovyTag
  @DisplayName("When run libdoc with java-lib from groovy script, a documentation should be generated.")
  fun testLibdocForJavaLibWithGroovy() {
    getFolderAction = { "libdoc" }
    val result = setupGroovyTest("run_libdoc_java_lib_test")
      .withArguments("assemble", "libdocRun")
      .build()

    runShouldSucceed(result)
    checkForHtmlDoc(::checkJavaLib)
  }

  @Test
  @KotlinTag
  @DisplayName("When run libdoc with folder containing multiple resource files " +
    "from kotlin script, each gets a single doc-file.")
  fun testLibdocFromResourceFolderKotlin() {
    getFolderAction = { "libdoc_res" }
    val result = setupKotlinTest("run_libdoc_res_test")
      .withArguments("libdocRun")
      .build()

    runShouldSucceed(result)
    checkForMultipleHtmlDocs(3, ::checkResourceFiles)
  }

  @Test
  @GroovyTag
  @DisplayName("When run libdoc with folder containing multiple resource files " +
    "from groovy script, each gets a single doc-file.")
  fun testLibdocFromResourceFolderGroovy() {
    getFolderAction = { "libdoc_res" }
    val result = setupGroovyTest("run_libdoc_res_test")
      .withArguments("libdocRun")
      .build()

    runShouldSucceed(result)
    checkForMultipleHtmlDocs(3, ::checkResourceFiles)
  }

  private fun checkJavaLib(file: File) {
    val content = file.readText(StandardCharsets.UTF_8)
    assertAll(
      { content should contain("This is the general library documentation of ALib.") },
      { content should contain("Say hello") },
      { content should contain("greeting") }
    )
  }

  private fun checkForHtmlDoc(contentChecker: (File) -> Unit) {
    val files = testProjectDir.root.walkBottomUp().filter {
      it.isFile && it.path.let { path ->
        path.contains("robotdoc") &&
          path.contains("libdoc") &&
          path.contains("libdoc.html")
      }
    }

    files shouldNot beNull()
    files.also {
      assertAll(
        { files shouldHaveSize 1 },
        {
          val file = files.first()
          contentChecker(file)
        }
      )
    }
  }

  private fun checkResourceFiles(file: File) {
    val content = file.readText(StandardCharsets.UTF_8)
    when (file.nameWithoutExtension.toLowerCase()) {
      "common" -> assertAll(
        { content should contain("Close browser") },
        { content should contain("Close the current active browser") },
        { content should contain("Open google") },
        { content should contain("Opens a browser") }
      )
      "countries" -> assertAll(
        { content should contain("Assert that '\${countries}' contains '\${expectedCountry}'") },
        { content should contain("Create default country germany") },
        { content should contain("Search country by name contains a country") },
        { content should contain("expectedCountry") }
      )
      "google" -> assertAll(
        { content should contain("Do google search") },
        { content should contain("Performs a google search with the given term") },
        { content should contain("The title should contain") },
        { content should contain("term") }
      )
      else -> throw AssertionError("Got unexpected file $file")
    }
  }

  private fun checkForMultipleHtmlDocs(expectedFiles: Int, contentChecker: (File) -> Unit) {
    val files = testProjectDir.root.walkBottomUp().filter {
      it.isFile && it.path.let { path ->
        path.contains("doc") &&
          path.contains("libdoc") &&
          path.endsWith(".html")
      }
    }

    files shouldNot beNull()
    val asserts =
      files.map { { contentChecker(it) } }.toMutableList().apply { add({ files shouldHaveSize expectedFiles }) }
    assertAll(asserts)
  }
}
