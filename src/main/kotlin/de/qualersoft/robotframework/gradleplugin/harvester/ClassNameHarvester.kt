package de.qualersoft.robotframework.gradleplugin.harvester

import org.python.google.common.reflect.ClassPath
import java.io.IOException

/**
 * Harvests resource (not class) names from the class path given an ant-like
 * pattern (considers '/' replaced with '.' though).
 */
class ClassNameHarvester : NameHarvester {
  override fun harvest(pattern: String): Set<String> {
    val minPatternIdx = HarvestUtils.calculateMinimumPatternIndex(pattern)
    val result = LinkedHashSet<String>()
    if (-1 < minPatternIdx) {
      result.addAll(findClassesByPattern(pattern))
    } else {
      result.add(pattern)
    }
    return result
  }

  private fun findClassesByPattern(pattern: String): Set<String> {
    val result = LinkedHashSet<String>()
    try {
      val cp = ClassPath.from(this.javaClass.classLoader)
      val ap = AntPatternClassPredicate(pattern)
      cp.allClasses.map { it.name }
        .filter { ap.apply(it) }
        .forEach { result.add(it) }
    } catch (ignore: IOException) {
      /* noop could not find any */
    }
    return result
  }
}