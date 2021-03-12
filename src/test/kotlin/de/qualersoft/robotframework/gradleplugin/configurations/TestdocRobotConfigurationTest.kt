package de.qualersoft.robotframework.gradleplugin.configurations

import de.qualersoft.robotframework.gradleplugin.PLUGIN_ID
import de.qualersoft.robotframework.gradleplugin.extensions.RobotFrameworkExtension
import de.qualersoft.robotframework.gradleplugin.robotframework
import io.kotest.matchers.collections.containsInOrder
import io.kotest.matchers.collections.shouldNotContainAnyOf
import io.kotest.matchers.should
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

class TestdocRobotConfigurationTest : ConfigurationTestBase() {

  @Test
  fun `Generate default run arguments`() {
    val result = applyConfig { }.generateArguments()
    result shouldNotContainAnyOf listOf(
      "--name", "--doc", "--metadata", "--settag",
      "--test", "--suite", "--include", "--exclude",
      "--argumentfiles", "--title"
    )
  }

  @Test
  fun `Generate with name`() {
    val result = applyConfigToArgList {
      it.name.set("A Name")
    }

    result should containsInOrder(listOf("--name", "A Name"))
  }

  @Test
  fun `Generate with doc`() {
    val result = applyConfigToArgList {
      it.doc.set("This is a documentation")
    }

    result should containsInOrder(listOf("--doc", "This is a documentation"))
  }

  @Test
  fun `Generate with single metadata`() {
    val result = applyConfigToArgList {
      it.metaData += ("mymeta" to "ThisIsAMetadata")
    }

    result should containsInOrder(listOf("--metadata", "mymeta:ThisIsAMetadata"))
  }

  @Test
  fun `Generate with multiple metadata`() {
    val result = applyConfigToArgList {
      it.metaData += mapOf("mymeta1" to "FirstMeta", "mymeta2" to "SecondMeta")
    }

    assertAll(
      { result should containsInOrder(listOf("--metadata", "mymeta1:FirstMeta")) },
      { result should containsInOrder(listOf("--metadata", "mymeta2:SecondMeta")) }
    )
  }

  @Test
  fun `Generate with single setTags`() {
    val result = applyConfigToArgList {
      it.setTags = mutableListOf("aTag")
    }

    result should containsInOrder(listOf("--settag", "aTag"))
  }

  @Test
  fun `Generate with multiple setTags`() {
    val result = applyConfigToArgList {
      it.setTags = mutableListOf("aTag", "bTag")
    }

    result should containsInOrder(listOf("--settag", "aTag", "--settag", "bTag"))
  }

  @Test
  fun `Generate with single test`() {
    val result = applyConfigToArgList {
      it.test = mutableListOf("aTest")
    }

    result should containsInOrder(listOf("--test", "aTest"))
  }

  @Test
  fun `Generate with multiple tests`() {
    val result = applyConfigToArgList {
      it.test = mutableListOf("aTest", "bTest")
    }

    result should containsInOrder(listOf("--test", "aTest", "--test", "bTest"))
  }

  @Test
  fun `Generate with single suite`() {
    val result = applyConfigToArgList {
      it.suite = mutableListOf("aSuite")
    }

    result should containsInOrder(listOf("--suite", "aSuite"))
  }

  @Test
  fun `Generate with multiple suites`() {
    val result = applyConfigToArgList {
      it.suite = mutableListOf("aSuite", "bSuite")
    }

    result should containsInOrder(listOf("--suite", "aSuite", "--suite", "bSuite"))
  }

  @Test
  fun `Generate with single include`() {
    val result = applyConfigToArgList {
      it.include = mutableListOf("aInclude")
    }

    result should containsInOrder(listOf("--include", "aInclude"))
  }

  @Test
  fun `Generate with multiple includes`() {
    val result = applyConfigToArgList {
      it.include = mutableListOf("aInclude", "bInclude")
    }

    result should containsInOrder(listOf("--include", "aInclude", "--include", "bInclude"))
  }

  @Test
  fun `Generate with single exclude`() {
    val result = applyConfigToArgList {
      it.exclude = mutableListOf("aExclude")
    }

    result should containsInOrder(listOf("--exclude", "aExclude"))
  }

  @Test
  fun `Generate with multiple excludes`() {
    val result = applyConfigToArgList {
      it.exclude = mutableListOf("aExclude", "bExclude")
    }

    result should containsInOrder(listOf("--exclude", "aExclude", "--exclude", "bExclude"))
  }

  @Test
  fun `Generate with single argFile`() {
    val result = applyConfigToArgList {
      it.argumentFiles = mutableListOf("aArgFile")
    }

    result should containsInOrder(listOf("--argumentfiles", "aArgFile"))
  }

  @Test
  fun `Generate with multiple argFiles`() {
    val result = applyConfigToArgList {
      it.argumentFiles = mutableListOf("aArgFile", "bArgFile")
    }

    result should containsInOrder(listOf("--argumentfiles", "aArgFile", "--argumentfiles", "bArgFile"))
  }

  @Test
  fun `Generate with title`() {
    val result = applyConfigToArgList {
      it.title.set("aTitle")
    }

    result should containsInOrder(listOf("--title", "aTitle"))
  }

  private val project: Project = ProjectBuilder.builder().build().also {
    it.pluginManager.apply(PLUGIN_ID)
  }
  private val rfExtension: RobotFrameworkExtension = project.robotframework()
  private fun applyConfigToArgList(conf: (TestdocRobotConfiguration) -> Unit): List<String> =
    applyConfig(conf).generateArguments().toList()

  private fun applyConfig(conf: (TestdocRobotConfiguration) -> Unit): TestdocRobotConfiguration {
    rfExtension.testdoc(conf)
    return rfExtension.testdoc.get()
  }
}