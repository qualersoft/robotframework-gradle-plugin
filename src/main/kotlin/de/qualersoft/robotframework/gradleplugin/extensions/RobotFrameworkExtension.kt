package de.qualersoft.robotframework.gradleplugin.extensions

import de.qualersoft.robotframework.gradleplugin.configurations.LibdocRobotConfiguration
import de.qualersoft.robotframework.gradleplugin.configurations.RebotRobotConfiguration
import de.qualersoft.robotframework.gradleplugin.configurations.RobotframeworkConfiguration
import de.qualersoft.robotframework.gradleplugin.configurations.RunRobotConfiguration
import de.qualersoft.robotframework.gradleplugin.utils.GradleProperty
import org.gradle.api.Action
import org.gradle.api.Project

open class RobotFrameworkExtension(project: Project) {

  val robotVersion by GradleProperty(project.objects,
      RobotframeworkConfiguration::class,
      RobotframeworkConfiguration(project))
  fun robotVersion(action: Action<RobotframeworkConfiguration>) {
    action.execute(robotVersion.get())
  }
  fun robotVersion(config: RobotframeworkConfiguration.() -> Unit) {
    robotVersion.get().apply(config)
  }

  val rebot by GradleProperty(project.objects, RebotRobotConfiguration::class, RebotRobotConfiguration(project))
  fun rebot(action: Action<RebotRobotConfiguration>) {
    action.execute(rebot.get())
  }
  fun rebot(config: RebotRobotConfiguration.() -> Unit) {
    rebot.get().apply(config)
  }

  val libdoc by GradleProperty(project.objects, LibdocRobotConfiguration::class, LibdocRobotConfiguration(project))
  fun libdoc(action: Action<LibdocRobotConfiguration>) {
    action.execute(libdoc.get())
  }
  fun libdoc(config: LibdocRobotConfiguration.() -> Unit) {
    libdoc.get().apply(config)
  }

  val robot by GradleProperty(project.objects, RunRobotConfiguration::class, RunRobotConfiguration(project))
  fun robot(action: Action<RunRobotConfiguration>) {
    action.execute(robot.get())
  }
  fun robot(config: RunRobotConfiguration.() -> Unit) {
    robot.get().apply(config)
  }
}