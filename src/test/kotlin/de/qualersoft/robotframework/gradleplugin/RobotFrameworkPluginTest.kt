package de.qualersoft.robotframework.gradleplugin

import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldNot
import io.kotlintest.specs.WordSpec
import org.gradle.testfixtures.ProjectBuilder

class RobotFrameworkPluginTest : WordSpec ({
  "Using the Plugin ID" should {
    "Apply the Plugin" {
      val project = ProjectBuilder.builder().build()
      project.pluginManager.apply("de.qualersoft.robotframework")

      val actual: RobotFrameworkPlugin? = project.plugins.getPlugin(RobotFrameworkPlugin::class.java)
      actual.shouldNotBeNull()
    }
  }
  //"Applying the Plugin" should {
  //  "Register the 'robotframework' extension" {
  //    val project = ProjectBuilder.builder().build()
  //    project.pluginManager.apply(RobotFrameworkPlugin::class.java)
//
  //    project.robotframework() shouldNot beNull()
  //  }
  //}
})
