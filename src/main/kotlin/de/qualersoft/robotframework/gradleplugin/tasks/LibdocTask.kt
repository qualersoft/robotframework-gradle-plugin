package de.qualersoft.robotframework.gradleplugin.tasks

import de.qualersoft.robotframework.gradleplugin.configurations.LibdocRobotConfiguration
import org.gradle.api.Action
import org.gradle.api.plugins.JavaPlugin
import java.io.File
import java.io.FileNotFoundException

open class LibdocTask : BasicRobotFrameworkTask() {

  init {
    description = "Create the rf documentation of this lib"
    group = "documentation"
  }

  private val libdoc = objectFactory.property(LibdocRobotConfiguration::class.java)
    .convention(rfExtension.libdoc)

  @Suppress("Unused")
  fun libdoc(action: Action<LibdocRobotConfiguration>) {
    action.execute(libdoc.get())
  }

  @Suppress("Unused")
  fun libdoc(config: LibdocRobotConfiguration.() -> Unit) {
    libdoc.get().apply(config)
  }

  @Throws(FileNotFoundException::class)
  override fun exec() {
    ensureLibraries()
    val tmp = rfArgs
    libdoc.get().generateRunArguments().forEach {
      rfArgs = tmp + it.toArray()
      executeRobotCommand("libdoc")
    }
  }

  private fun ensureLibraries() {
    val jars = getLibJars()
    jars.forEach {
      classpath(project.files(it))
    }
  }

  private fun getLibJars(): Collection<File> {
    val files = libdoc.get().additionalPythonPaths.files.toMutableList()

    project.configurations.getByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME).also {
      files += it.files.toList()
    }
    return files
  }
}