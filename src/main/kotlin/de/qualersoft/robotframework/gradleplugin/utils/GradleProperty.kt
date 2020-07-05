package de.qualersoft.robotframework.gradleplugin.utils

import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

internal class GradleProperty<T, V : Any> {

  private val property: Property<V>

  constructor(objects: ObjectFactory, type: KClass<V>, default: V? = null) {
    property = createProperty(objects, type).apply {
      convention(default)
    }
  }

  constructor(objects: ObjectFactory, type: KClass<V>, provider: Provider<V>) {
    property = createProperty(objects, type).apply {
      convention(provider)
    }
  }

  private fun createProperty(objects: ObjectFactory, type: KClass<V>) = objects.property(type.java)

  operator fun getValue(thisRef: T, property: KProperty<*>): Provider<V> = this.property
  operator fun setValue(thisRef: T, property: KProperty<*>, value: V?) = this.property.set(value)
  operator fun setValue(thisRef: T, property: KProperty<*>, provider: Provider<V>) = this.property.set(provider)
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

internal class GradleFileProperty<T> {

  private val property: RegularFileProperty

  constructor(objects: ObjectFactory, default: RegularFile? = null) {
    property = createProperty(objects).convention(default)
  }

  constructor(objects: ObjectFactory, provider: Provider<out RegularFile>) {
    property = createProperty(objects).convention(provider)
  }

  constructor(objects: ObjectFactory, default: File) {
    property = createProperty(objects).convention(
        objects.directoryProperty().file(default.absolutePath)
    )
  }

  private fun createProperty(objects: ObjectFactory) = objects.fileProperty()

  operator fun getValue(thisRef: T, property: KProperty<*>): RegularFileProperty = this.property
  operator fun setValue(thisRef: T, property: KProperty<*>, value: RegularFileProperty?) = this.property.set(value)
}

internal class GradleDirectoryProperty<T> {

  private val property: DirectoryProperty

  constructor(objects: ObjectFactory, default: Directory? = null) {
    property = createProperty(objects).convention(default)
  }

  constructor(objects: ObjectFactory, provider: Provider<out Directory>) {
    property = createProperty(objects).convention(provider)
  }

  constructor(objects: ObjectFactory, default: File) {
    property = createProperty(objects).convention(
        objects.directoryProperty().dir(default.absolutePath)
    )
  }

  private fun createProperty(objects: ObjectFactory) = objects.directoryProperty()

  operator fun getValue(thisRef: T, property: KProperty<*>): DirectoryProperty = this.property
  operator fun setValue(thisRef: T, property: KProperty<*>, value: DirectoryProperty) = this.property.set(value)
}