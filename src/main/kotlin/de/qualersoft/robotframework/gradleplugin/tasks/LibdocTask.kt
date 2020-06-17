package de.qualersoft.robotframework.gradleplugin.tasks

import de.qualersoft.robotframework.gradleplugin.configurations.LibdocRobotConfiguration
import org.gradle.api.Action
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.compile.JavaCompile
import org.robotframework.RobotFramework
import java.io.File
import java.io.FileNotFoundException
import java.net.URL
import java.net.URLClassLoader




open class LibdocTask : BasicRobotFrameworkTask() {

  @Internal
  @Suppress("private")
  var libdoc = extension.libdoc
  @Suppress("Unused")
  fun libdoc(action: Action<LibdocRobotConfiguration>) {
    action.execute(libdoc)
  }
  @Suppress("Unused")
  fun libdoc(config: LibdocRobotConfiguration.() -> Unit) {
    libdoc.apply(config)
  }

  @Throws(FileNotFoundException::class)
  override fun exec() {
    ensureJython()
    rfArgs = (libdoc.generateRunArguments() + rfArgs) as MutableList<String>
    executeRobotCommand("libdoc")
  }

  private fun ensureJython() {
    if (null == System.getenv("JYTHONPATH")) {
      val jar = getJythonJar()
      classpath += project.files(jar)
    }
  }

  private fun getJythonJar(): File? = project.configurations.findByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)?.find {
    it.isFile && it.name.contains("jython")
  }
}