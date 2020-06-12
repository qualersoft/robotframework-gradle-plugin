package de.qualersoft.robotframework.gradleplugin.harvester

import org.apache.tools.ant.DirectoryScanner
import java.io.File
import java.util.*

class SourceFileNameHarvester (private val baseDir: File) : NameHarvester {
  override fun harvest(pattern: String): Set<String> {
    val minPatternIndex = HarvestUtils.calculateMinimumPatternIndex(pattern)
    val lastSlashBeforePatternSymbol = pattern.lastIndexOf('/', minPatternIndex)
    val lastBackslashBeforePatternSymbol = pattern.lastIndexOf('\\', minPatternIndex)

    val maxSlashIndex = lastSlashBeforePatternSymbol.coerceAtMost(lastBackslashBeforePatternSymbol)

    var baseDirectory = ""
    // Determine whether to provide the project base dir.
    if (!HarvestUtils.isAbsolutePathFragment(pattern)) {
      baseDirectory = baseDir.absolutePath.toString() + File.separator
    }

    // Parse out the additional directory and pattern parts.
    var patternString = ""
    if (0 < maxSlashIndex) {
      baseDirectory += pattern.substring(0, maxSlashIndex + 1)
      if (maxSlashIndex + 1 < pattern.length) patternString = pattern.substring(maxSlashIndex + 1)
    } else patternString = pattern

    // pattern that we need to expand.
    val scanner = DirectoryScanner().apply {
      setBasedir(baseDirectory)
      isCaseSensitive = true
      setIncludes(arrayOf(patternString))
    }
    scanner.scan()

    val includedFiles = scanner.includedFiles
    val result = LinkedHashSet<String>()
    val bDir: File = scanner.basedir
    for (iF in includedFiles) {
      val tmp = File(bDir, iF)
      result.add(tmp.absolutePath)
    }
    return result
  }
}