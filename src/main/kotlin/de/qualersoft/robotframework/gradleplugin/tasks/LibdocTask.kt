package de.qualersoft.robotframework.gradleplugin.tasks

import de.qualersoft.robotframework.gradleplugin.configurations.LibdocRobotConfiguration
import org.gradle.api.Action
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Internal
import java.io.File
import java.io.FileNotFoundException

open class LibdocTask : BasicRobotFrameworkTask() {

  @Internal
  @Suppress("private")
  var libdoc = extension.libdoc
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
      rfArgs = (it.toArray() + tmp).toMutableList()
      executeRobotCommand("libdoc")
    }
  }

  private fun ensureLibraries() {
    val jars = getLibJars()
    jars?.forEach {
      classpath(project.files(it))
    }
  }

  private fun getLibJars(): Collection<File>? {
    val files = libdoc.get().additionalPythonPaths.files.toMutableList()

    project.configurations.findByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)?.also {
      files += it.files.toList()
    }
    return files
  }
}