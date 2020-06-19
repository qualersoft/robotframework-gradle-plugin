package de.qualersoft.robotframework.gradleplugin.tasks

import de.qualersoft.robotframework.gradleplugin.configurations.LibdocRobotConfiguration
import org.gradle.api.Action
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Internal
import org.robotframework.RobotFramework
import java.io.File
import java.io.FileNotFoundException
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
    createClassLoader()
    libdoc.additionalPythonPaths.from( getJavaLibJar() )
    rfArgs = (libdoc.generateRunArguments() + rfArgs) as MutableList<String>
    //executeRobotCommand("libdoc")
    val args = (listOf("libdoc") + rfArgs).toTypedArray()
    RobotFramework.run(args)
  }

  private fun createClassLoader() {
    val jars = listOf(getJythonJar(), getJavaLibJar())
    val urls = jars.filterNotNull().map { it.toURI().toURL() }
    URLClassLoader(urls.toTypedArray(), this.javaClass.classLoader)
  }

  private fun getJythonJar(): File? = project.configurations
          .findByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)?.find {
    it.isFile && it.name.contains("jython")
  }

  private fun getJavaLibJar(): File? = project.configurations
          .findByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)?.find {
    it.isFile && it.name.contains("javalib-core")
  }
}