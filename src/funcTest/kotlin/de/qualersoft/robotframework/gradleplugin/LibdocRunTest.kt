package de.qualersoft.robotframework.gradleplugin

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.beNull
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
  @GroovyTag
  @DisplayName("When run libdoc for robot-file from groovy script, a documentation should be generated.")
  fun testGenerateDocForRobotWithGroovy() {
    getFolderAction = { "libdoc" }
    val result = setupGroovyTest("run_minimal_libdoc_robot_test")
      .withArguments("libdocRun")
      .build()
    println(result.output)
    /* TODO 'ImportError: No module named robot'
    result.output shouldNot contain("Documenting Java test libraries requires Jython")
    checkForHtmlDoc(::checkRobotLib)
    */
  }

  @Test
  @KotlinTag
  @DisplayName("When run libdoc for robot-file from kotlin script, a documentation should be generated.")
  fun testGenerateDocForRobotWithKotlin() {
    getFolderAction = { "libdoc" }
    val result = setupGroovyTest("run_minimal_libdoc_robot_test")
      .withArguments("libdocRun")
      .build()
    println(result.output)
    /* TODO 'ImportError: No module named robot'
    result.output shouldNot contain("Documenting Java test libraries requires Jython")
    checkForHtmlDoc(::checkRobotLib)
    */
  }

  @Test
  @GroovyTag
  @DisplayName("When run libdoc for java-file from groovy script, a documentation should be generated.")
  fun testGenerateDocForJavaWithGroovy() {
    getFolderAction = { "libdoc" }
    val result = setupGroovyTest("run_minimal_libdoc_java_test")
      .withArguments("libdocRun")
      .build()
    println(result.output)
    /* TODO libdoc for java files is atm not working
    result.output shouldNot contain("Documenting Java test libraries requires Jython")
    checkForHtmlDoc(::checkJavaLib)
    */
  }

  @Test
  @KotlinTag
  @DisplayName("When run libdoc for java-file from kotlin script, a documentation should be generated.")
  fun testGenerateDocForJavaWithKotlin() {
    getFolderAction = { "libdoc" }
    val result = setupKotlinTest("run_minimal_libdoc_java_test")
      .withArguments("libdocRun")
      .build()
    println(result.output)
    /* TODO libdoc for java files is atm not working
    result.output shouldNot contain("Documenting Java test libraries requires Jython")
    checkForHtmlDoc(::checkJavaLib)
    */
  }

  private fun checkRobotLib(file: File) {
    val content = file.readText(StandardCharsets.UTF_8)
    assertAll(
      { content should contain("Suite description") },
      { content should contain("Write stuff to the output") }
    )
  }

  private fun checkJavaLib(file: File) {
    val content = file.readText(StandardCharsets.UTF_8)
    assertAll(
      { content should contain("Say hello") },
      { content should contain("greeting") }
    )
  }

  private fun checkForHtmlDoc(contentChecker: (File) -> Unit) {
    val files = testProjectDir.root.listFiles { it:File ->
      it.isFile && it.path.let {
        it.contains("robotframework") &&
          it.contains("libdoc") &&
          it.contains("libdoc.html")
      }
    }

    files shouldNot beNull()
    files!!.also {
      assertAll(
        { files shouldHaveSize(1) },
        { val file = files.first()
          contentChecker(file)
        }
      )
    }
  }
}