package de.qualersoft.robotframework.gradleplugin.tasks

import de.qualersoft.robotframework.gradleplugin.utils.GradleStringListProperty
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSetContainer

abstract class  BasicRobotFrameworkTask : JavaExec() {

  var rfArgs by GradleStringListProperty(project)

  internal fun executeRobotCommand(cmd: String, vararg additionalArgs: String) {
    classpath = project.extensions.getByType(SourceSetContainer::class.java).getByName("main").runtimeClasspath
    args = listOf(cmd) + rfArgs + additionalArgs
    super.exec()
  }
}