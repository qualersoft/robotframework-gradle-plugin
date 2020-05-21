package de.qualersoft.robotframework.gradleplugin.utils

import org.gradle.api.ExtensiblePolymorphicDomainObjectContainer
import org.gradle.api.Named
import org.gradle.api.Project
import org.gradle.api.internal.CollectionCallbackActionDecorator
import org.gradle.api.internal.DefaultPolymorphicDomainObjectContainer
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.internal.reflect.Instantiator
import org.gradle.util.GradleVersion

class GradleUtils {
  companion object {
    private val is_Gradle_5_1_or_older: Boolean =  GradleVersion.version("5.1") >= GradleVersion.current()
    fun <T : Named> polymorphicContainer(project: Project, type: Class<T>):
      ExtensiblePolymorphicDomainObjectContainer<T> {
      val instantiator = (project as ProjectInternal).services.get(Instantiator::class.java)
      val result = if (is_Gradle_5_1_or_older) {
        instantiator.newInstance(DefaultPolymorphicDomainObjectContainer::class.java, type, instantiator,
          CollectionCallbackActionDecorator.NOOP)
      } else {
        instantiator.newInstance(DefaultPolymorphicDomainObjectContainer::class.java, type, instantiator)
      }
      return result as ExtensiblePolymorphicDomainObjectContainer<T>
    }
  }

  private constructor() // util class
}