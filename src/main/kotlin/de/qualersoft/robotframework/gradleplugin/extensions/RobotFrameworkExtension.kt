package de.qualersoft.robotframework.gradleplugin.extensions

import de.qualersoft.robotframework.gradleplugin.configurations.LibdocRobotConfiguration
import de.qualersoft.robotframework.gradleplugin.configurations.RebotRobotConfiguration
import de.qualersoft.robotframework.gradleplugin.configurations.RobotframeworkConfiguration
import de.qualersoft.robotframework.gradleplugin.configurations.RunRobotConfiguration
import de.qualersoft.robotframework.gradleplugin.utils.GradleProperty
import org.gradle.api.Action
import org.gradle.api.Project

open class RobotFrameworkExtension(project: Project) {

  val robotVersion by GradleProperty(project, RobotframeworkConfiguration::class, RobotframeworkConfiguration(project))
  fun robotVersion(action: Action<RobotframeworkConfiguration>) {
    action.execute(robotVersion)
  }
  fun robotVersion(config: RobotframeworkConfiguration.() -> Unit) {
    robotVersion.apply(config)
  }

  val rebot by GradleProperty(project, RebotRobotConfiguration::class, RebotRobotConfiguration(project))
  @Suppress("Unused")
  fun rebot(action: Action<RebotRobotConfiguration>) {
    action.execute(rebot)
  }
  @Suppress("Unused")
  fun rebot(config: RebotRobotConfiguration.() -> Unit) {
    rebot.apply(config)
  }

  val libdoc by GradleProperty(project, LibdocRobotConfiguration::class, LibdocRobotConfiguration(project))
  @Suppress("Unused")
  fun libdoc(action: Action<LibdocRobotConfiguration>) {
    action.execute(libdoc)
  }
  @Suppress("Unused")
  fun libdoc(config: LibdocRobotConfiguration.() -> Unit) {
    libdoc.apply(config)
  }

  val robot by GradleProperty(project, RunRobotConfiguration::class, RunRobotConfiguration(project))
  @Suppress("Unused")
  fun robot(action: Action<RunRobotConfiguration>) {
    action.execute(robot)
  }
  @Suppress("Unused")
  fun robot(config: RunRobotConfiguration.() -> Unit) {
    robot.apply(config)
  }
}