package de.qualersoft.robotframework.gradleplugin.utils

import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

internal class GradleProperty<T, V:Any>(
  project: Project,
  type: KClass<V>,
  default: V
) {
  private val property: Property<V> = project.objects.property(type.java).apply {
    set(default)
  }

  operator fun getValue(thisRef: T, property: KProperty<*>): V = this.property.get()
  operator fun setValue(thisRef: T, property: KProperty<*>, value: V) = this.property.set(value)
}

internal class GradleNullableProperty<T, V:Any>(
  project: Project,
  type: KClass<V>,
  default: V? = null
) {
  private val property: Property<V?> = project.objects.property(type.java).apply {
    set(default)
  }

  operator fun getValue(thisRef: T, property: KProperty<*>): V? = this.property.orNull
  operator fun setValue(thisRef: T, property: KProperty<*>, value: V?) = this.property.set(value)
}

internal class GradleListProperty<T, V:Any>(
  project: Project,
  type: KClass<V>,
  default: List<V> = mutableListOf()
){
  private val property: ListProperty<V> = project.objects.listProperty(type.java).apply {
    set(default)
  }
  operator fun getValue(thisRef: T, property: KProperty<*>): MutableList<V> = this.property.get()
  operator fun setValue(thisRef: T, property: KProperty<*>, value: MutableList<V>) = this.property.set(value)
}

internal class GradleStringListProperty<T>(
  project: Project,
  default: List<String> = mutableListOf()
) {
  private val property: ListProperty<String> = project.objects.listProperty(String::class.java).apply {
    set(default)
  }
  operator fun getValue(thisRef: T, property: KProperty<*>): MutableList<String> = this.property.get()
  operator fun setValue(thisRef: T, property: KProperty<*>, value: MutableList<String>) = this.property.set(value)
}

internal class GradleStringMapProperty<T>(
  project: Project,
  default: Map<String, String> = mapOf()
) {

  private val property: MapProperty<String, String> = project.objects.mapProperty(
    String::class.java, String::class.java).apply {
    set(default)
  }
  operator fun getValue(thisRef: T, property: KProperty<*>): Map<String, String> = this.property.get()
  operator fun setValue(thisRef: T, property: KProperty<*>, value: Map<String, String>) = this.property.set(value)
}

internal class GradleFileNullableProperty<T>(
  project: Project,
  default: File? = null
) {

  private val property = project.objects.property(File::class.java).apply {
    set(default)
  }
  operator fun getValue(thisRef: T, property: KProperty<*>): File? = this.property.orNull
  operator fun setValue(thisRef: T, property: KProperty<*>, value: File?) = this.property.set(value)
}

internal class GradleFileProperty<T>(
  project: Project,
  default: File
) {

  private val property: Property<File> = project.objects.property(File::class.java).apply {
    set(default)
  }
  operator fun getValue(thisRef: T, property: KProperty<*>): File = this.property.get()
  operator fun setValue(thisRef: T, property: KProperty<*>, value: File) = this.property.set(value)
}