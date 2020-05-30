package de.qualersoft.robotframework.gradleplugin

import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Tag
import java.io.File
import java.io.InputStream

private const val EXT_GR = ".gradle"
private const val EXT_KT = "$EXT_GR.kts"

open class BaseRobotFrameworkFunctionalTest {

  companion object { const val BUG492 = "Accessing properties of extension in kotlin dsl an error is raised see https://github.com/gradle/kotlin-dsl-samples/issues/492" }

  @Tag("groovy")
  annotation class GroovyTag

  @Tag("kotlin")
  annotation class KotlinTag


  protected val testProjectDir = TemporaryFolder()

  @AfterEach
  fun cleanup() {
    testProjectDir.delete()
  }

  /**
   * Meant to be overridden if required.
   * If not `null` the whole folder will be copied.
   * @see setupGroovyTest
   * @see setupKotlinTest
   */
  protected open fun rootFolder(): String? = null


  protected fun setupKotlinTest(baseFileName: String): GradleRunner {
    copyTestFileToTemp(baseFileName, EXT_KT)
    return createRunner()
  }

  protected fun setupGroovyTest(baseFileName: String): GradleRunner {
    copyTestFileToTemp(baseFileName, EXT_GR)
    return createRunner()
  }

  private fun createRunner() = GradleRunner.create().withProjectDir(testProjectDir.root)
    // Attention: do not enable debug! Details see https://github.com/gradle/gradle/issues/6862
    .withPluginClasspath()

  private fun copyTestFileToTemp(resource: String, ext: String): File {
    var res = resource + ext
    // if we have a rootfolder
    rootFolder()?.also {
      res = "$it/$res"
    }

    val file = File(RobotFrameworkPluginFuncTest::class.java.classLoader.getResource(res)!!.file)
    testProjectDir.create()
    val result = testProjectDir.newFile("build$ext")
    file.inputStream().use { input ->
      result.outputStream().use { output -> input.copyTo(output) }
    }

    // copy rest of data to temp dir
    rootFolder()?.also {
      val folder = File(this.javaClass.classLoader.getResource(it)!!.file)
      folder.listFiles { f ->
        // only get those files not starting with the build-script resource name
        !(f.isFile && f.name.startsWith(resource))
      }?.forEach { f ->
        f.copyRecursively(if (f.isFile) {
          File(testProjectDir.root, f.name)
        } else {
          testProjectDir.root
        })
      }
    }
    return result
  }
}