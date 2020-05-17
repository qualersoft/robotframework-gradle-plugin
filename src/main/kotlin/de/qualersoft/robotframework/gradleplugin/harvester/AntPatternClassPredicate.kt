package de.qualersoft.robotframework.gradleplugin.harvester

import org.apache.tools.ant.DirectoryScanner
import org.python.google.common.base.Predicate
import java.io.File

class AntPatternClassPredicate(aPattern: String) : Predicate<String> {
  private val pattern: String = aPattern.replace(".", File.separator)

  override fun apply(target: String?): Boolean {
    if (null == target) {
      throw IllegalArgumentException("target must not be null!")
    }
    var compatibleTarget = if (target.endsWith(".class")) {
      val classSuffixIdx = target.lastIndexOf(".class")
      target.substring(0, classSuffixIdx)
    } else {
      target
    }
    compatibleTarget = compatibleTarget.replace(".", File.separator)
    return DirectoryScanner.match(pattern, compatibleTarget)
  }
}