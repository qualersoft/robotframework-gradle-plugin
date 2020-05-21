package de.qualersoft.robotframework.gradleplugin.harvester

import org.python.google.common.reflect.ClassPath
import java.io.IOException

class ResourceNameHarvester : NameHarvester {
  override fun harvest(pattern: String): Set<String> {
    val minPatternIdx = HarvestUtils.calculateMinimumPatternIndex(pattern)
    val result = LinkedHashSet<String>()
    if (-1 < minPatternIdx) {
      result.addAll(findResourcesByPattern(pattern))
    } else {
      // No pattern, add as direct resource to deal with later
      result.add(pattern)
    }
    return result
  }

  private fun findResourcesByPattern(pattern: String): Set<String> {
    val result = LinkedHashSet<String>()
    try {
      val cp = ClassPath.from(this.javaClass.classLoader)
      val ap = AntPatternClassPredicate(pattern)
      cp.resources.map { it.resourceName }
        .filter { ap.apply(it) }
        .forEach { result.add(it) }
    } catch (ignore: IOException) {
      /* noop could not find any */
    }
    return result
  }
}