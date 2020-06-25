package de.qualersoft.robotframework.gradleplugin.utils

import org.gradle.api.file.Directory
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

internal class GradleProperty<T, V : Any>(
    objects: ObjectFactory,
    type: KClass<V>,
    default: V
) {
  private val property: Property<V> = objects.property(type.java).apply {
    set(default)
  }

  operator fun getValue(thisRef: T, property: KProperty<*>): V = this.property.get()
  operator fun setValue(thisRef: T, property: KProperty<*>, value: V) = this.property.set(value)
}

internal class GradleNullableProperty<T, V : Any>(
    objects: ObjectFactory,
    type: KClass<V>,
    default: V? = null
) {
  private val property: Property<V?> = objects.property(type.java).apply {
    set(default)
  }

  operator fun getValue(thisRef: T, property: KProperty<*>): V? = this.property.orNull
  operator fun setValue(thisRef: T, property: KProperty<*>, value: V?) = this.property.set(value)
}

internal class GradleStringListProperty<T>(
    objects: ObjectFactory,
    default: List<String> = mutableListOf()
) {
  private val property: ListProperty<String> = objects.listProperty(String::class.java).apply {
    set(default)
  }

  operator fun getValue(thisRef: T, property: KProperty<*>): MutableList<String> = this.property.get()
  operator fun setValue(thisRef: T, property: KProperty<*>, value: MutableList<String>) = this.property.set(value)
}

internal class GradleStringMapProperty<T>(
    objects: ObjectFactory,
    default: Map<String, String> = mapOf()
) {

  private val property: MapProperty<String, String> = objects.mapProperty(
      String::class.java, String::class.java).apply {
    set(default)
  }

  operator fun getValue(thisRef: T, property: KProperty<*>): Map<String, String> = this.property.get()
  operator fun setValue(thisRef: T, property: KProperty<*>, value: Map<String, String>) = this.property.set(value)
}

internal class GradleFileNullableProperty<T>(
    objects: ObjectFactory,
    default: File? = null
) {

  private val property = objects.property(File::class.java).apply {
    set(default)
  }

  operator fun getValue(thisRef: T, property: KProperty<*>): File? = this.property.orNull
  operator fun setValue(thisRef: T, property: KProperty<*>, value: File?) = this.property.set(value)
}

internal class GradleFileProperty<T>(
    objects: ObjectFactory,
    default: File
) {

  private val property: Property<File> = objects.property(File::class.java).apply {
    set(default)
  }

  operator fun getValue(thisRef: T, property: KProperty<*>): File = this.property.get()
  operator fun setValue(thisRef: T, property: KProperty<*>, value: File) = this.property.set(value)
  operator fun setValue(thisRef: T, property: KProperty<*>, value: Provider<File>) = this.property.set(value)
}

internal class GradleDirectoryProperty<T>(
    objects: ObjectFactory,
    default: File
) {
  private val property = objects.directoryProperty().apply {
    set(default)
  }

  operator fun getValue(thisRef: T, property: KProperty<*>): Directory = this.property.get()
  operator fun setValue(thisRef: T, property: KProperty<*>, value: File) = this.property.set(value)
  operator fun setValue(thisRef: T, property: KProperty<*>, value: Directory) = this.property.set(value)
  operator fun setValue(thisRef: T, property: KProperty<*>, value: Provider<Directory>) = this.property.set(value)
}