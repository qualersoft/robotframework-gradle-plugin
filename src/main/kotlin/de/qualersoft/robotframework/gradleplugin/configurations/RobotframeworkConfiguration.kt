package de.qualersoft.robotframework.gradleplugin.configurations

import groovy.lang.Closure
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.provider.Property

class RobotframeworkConfiguration(private val project: Project) {

  fun applyTo(rtConf: Configuration) {
    rtConf.dependencies.add(createRobotLibDependency())
  }

  private fun createRobotLibDependency(): Dependency {
    val depNot = createDependencyNotation()
    val cc = configureClosure
    return if (null != cc) {
      project.dependencies.create(depNot, cc)
    } else {
      project.dependencies.create(depNot)
    }
  }

  internal fun createDependencyNotation(): Map<String, String> = mutableMapOf<String, String>().also { res ->
    res["group"] = group
    res["name"] = name
    res["version"] = version
    classifier?.also { res["classifier"] = it }
    ext?.also { res["ext"] = it }
  }

  var version: String = "3.2"
  var group: String = "org.robotframework"
  var name: String = "robotframework"
  var classifier: String? = null
  var ext: String? = null
  var configureClosure: Closure<Any>? = null
  var mainClass: Property<String> = project.objects.property(String::class.java).also {
    it.set("org.robotframework.RobotFramework")
  }
}