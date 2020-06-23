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


const val BUILD_SUCCESSFUL = "BUILD SUCCESSFUL"

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
    println(result.output)
    result.output should contain(BUILD_SUCCESSFUL)
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
    println(result.output)
    result.output should contain(BUILD_SUCCESSFUL)
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
    println(result.output)
    result.output should contain(BUILD_SUCCESSFUL)
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
    println(result.output)
    result.output should contain(BUILD_SUCCESSFUL)
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
    println(result.output)
    result.output should contain(BUILD_SUCCESSFUL)
    checkForHtmlDoc(::checkJavaLib)
  }

  @Test
  @GroovyTag
  @DisplayName("When run libdoc with java-lib from groovy script, a documentation should be generated.")
  fun testLibdocForJavaLibWithGroovy() {
    getFolderAction = { "libdoc" }
    val result = setupGroovyTest("run_libdoc_java_lib_test")
        .withArguments("assemble", "libdocRun")
        .withDebug(true)
        .build()
    println(result.output)
    result.output should contain(BUILD_SUCCESSFUL)
    checkForHtmlDoc(::checkJavaLib)
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
        path.contains("robotframework") &&
            path.contains("libdoc") &&
            path.contains("libdoc.html")
      }
    }

    files shouldNot beNull()
    files.also {
      assertAll(
          { files shouldHaveSize (1) },
          {
            val file = files.first()
            contentChecker(file)
          }
      )
    }
  }
}