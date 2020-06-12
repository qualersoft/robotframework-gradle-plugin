package de.qualersoft.robotframework.gradleplugin.extensions

import de.qualersoft.robotframework.gradleplugin.PLUGIN_ID
import de.qualersoft.robotframework.gradleplugin.configurations.LibdocRobotConfiguration
import de.qualersoft.robotframework.gradleplugin.configurations.RebotRobotConfiguration
import de.qualersoft.robotframework.gradleplugin.configurations.RobotframeworkConfiguration
import de.qualersoft.robotframework.gradleplugin.configurations.RunRobotConfiguration
import de.qualersoft.robotframework.gradleplugin.robotframework
import io.kotest.matchers.shouldBe
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

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

    sut.robotVersion.name shouldBe "robotVersion lambda name"
  }

  @Test
  fun `robotVersion configuration can be done by action`() {
    val action = Action<RobotframeworkConfiguration> {
      it.name = "robotVersion action name"
    }
    sut.robotVersion(action)

    sut.robotVersion.name shouldBe "robotVersion action name"
  }

  @Test
  fun `libdoc configuration can be done by lambda`() {
    sut.libdoc {
      name = "libdoc lambda name"
    }

    sut.libdoc.name shouldBe "libdoc lambda name"
  }

  @Test
  fun `libdoc configuration can be done with action`() {
    val action = Action<LibdocRobotConfiguration>() {
      it.name = "libdoc action name"
    }
    sut.libdoc(action)

    sut.libdoc.name shouldBe "libdoc action name"
  }

  @Test
  fun `robot configuration can be done by lambda`() {
    sut.robot {
      name = "robot lambda name"
    }

    sut.robot.name shouldBe "robot lambda name"
  }

  @Test
  fun `robot configuration can be done by action`() {
    val action = Action<RunRobotConfiguration> {
      it.name = "robot action name"
    }
    sut.robot(action)

    sut.robot.name shouldBe "robot action name"
  }

  @Test
  fun `rebot configuration can be done by lambda`() {
    sut.rebot {
      name = "rebot lambda name"
    }

    sut.rebot.name shouldBe "rebot lambda name"
  }

  @Test
  fun `rebot configuration can be done by action`() {
    val action = Action<RebotRobotConfiguration> {
      it.name = "rebot action name"
    }
    sut.rebot(action)

    sut.rebot.name shouldBe "rebot action name"
  }
}