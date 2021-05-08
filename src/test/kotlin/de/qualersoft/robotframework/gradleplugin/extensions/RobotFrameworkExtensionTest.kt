package de.qualersoft.robotframework.gradleplugin.extensions

import de.qualersoft.robotframework.gradleplugin.PLUGIN_ID
import de.qualersoft.robotframework.gradleplugin.configurations.LibdocRobotConfiguration
import de.qualersoft.robotframework.gradleplugin.configurations.RebotRobotConfiguration
import de.qualersoft.robotframework.gradleplugin.configurations.RobotframeworkConfiguration
import de.qualersoft.robotframework.gradleplugin.configurations.RunRobotConfiguration
import de.qualersoft.robotframework.gradleplugin.configurations.TestdocRobotConfiguration
import de.qualersoft.robotframework.gradleplugin.configurations.TidyRobotConfiguration
import de.qualersoft.robotframework.gradleplugin.robotframework
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

/**
 * Remark: This is just a simple technical test class.
 * Real testing of extension and configuration of it's
 * configurations will be done in functional tests.
 */
class RobotFrameworkExtensionTest {

  private val project: Project = ProjectBuilder.builder().build().also {
    it.pluginManager.apply(JavaPlugin::class.java)
    it.repositories.mavenCentral()
    it.pluginManager.apply(PLUGIN_ID)
  }

  private val sut: RobotFrameworkExtension = project.robotframework()

  @Test
  fun `robotVersion configuration can be done by lambda`() {
    sut.robotVersion {
      name = "robotVersion lambda name"
    }

    sut.robotVersion.get().name shouldBe "robotVersion lambda name"
  }

  @Test
  fun `robotVersion configuration can be done by action`() {
    val action = Action<RobotframeworkConfiguration> {
      it.name = "robotVersion action name"
    }
    sut.robotVersion(action)

    sut.robotVersion.get().name shouldBe "robotVersion action name"
  }

  @Test
  fun `libdoc configuration can be done by lambda`() {
    sut.libdoc {
      name.set("libdoc lambda name")
    }

    sut.libdoc.get().name.get() shouldBe "libdoc lambda name"
  }

  @Test
  fun `libdoc configuration can be done with action`() {
    val action = Action<LibdocRobotConfiguration>() {
      it.name.set("libdoc action name")
    }
    sut.libdoc(action)

    sut.libdoc.get().name.get() shouldBe "libdoc action name"
  }

  @Test
  fun `robot configuration can be done by lambda`() {
    sut.robot {
      name.set("robot lambda name")
    }

    sut.robot.get().name.get() shouldBe "robot lambda name"
  }

  @Test
  fun `robot configuration can be done by action`() {
    val action = Action<RunRobotConfiguration> {
      it.name.set("robot action name")
    }
    sut.robot(action)

    sut.robot.get().name.get() shouldBe "robot action name"
  }

  @Test
  fun `rebot configuration can be done by lambda`() {
    sut.rebot {
      name.set("rebot lambda name")
    }

    sut.rebot.get().name.get() shouldBe "rebot lambda name"
  }

  @Test
  fun `rebot configuration can be done by action`() {
    val action = Action<RebotRobotConfiguration> {
      it.name.set("rebot action name")
    }
    sut.rebot(action)

    sut.rebot.get().name.get() shouldBe "rebot action name"
  }

  @Test
  fun `testdoc configuration can be done by lambda`() {
    sut.testdoc {
      setTags = mutableListOf("testTag")
    }

    sut.testdoc.get().setTags shouldContain "testTag"
  }

  @Test
  fun `testdoc configuration can be done by action`() {
    val action = Action<TestdocRobotConfiguration> {
      it.setTags = mutableListOf("aTestTag")
    }
    sut.testdoc(action)

    sut.testdoc.get().setTags shouldContain "aTestTag"
  }

  @Test
  fun `tidy configuration can be done by lambda`() {
    sut.tidy {
      spacecount.set(100)
    }

    sut.tidy.get().spacecount.get() shouldBe 100
  }

  @Test
  fun `tidy configuration can be done by action`() {
    val action = Action<TidyRobotConfiguration> {
      it.spacecount.set(200)
    }
    sut.tidy(action)

    sut.tidy.get().spacecount.get() shouldBe 200
  }
}
