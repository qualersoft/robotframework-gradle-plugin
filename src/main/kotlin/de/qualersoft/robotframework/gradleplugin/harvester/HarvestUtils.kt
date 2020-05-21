package de.qualersoft.robotframework.gradleplugin.harvester

import java.io.File

private const val JAVA_FILE_EXT = ".java"
private const val KOTLIN_FILE_EXT = ".kt"
private const val GROOVY_FILE_EXT = ".groovy"

class HarvestUtils {
  companion object {
    fun extractName(harvestedName: String): String {
      var result = ""
      val idxOfSlash = harvestedName.lastIndexOf("/")
      val idxOfBackSlash = harvestedName.lastIndexOf("\\")
      if ((-1 < idxOfSlash) || (-1 < idxOfBackSlash)) {
        // we deal with a file path
        val idx = idxOfSlash.coerceAtMost(idxOfBackSlash)
        if (harvestedName.length != idx + 1) {
          result = extractNameFromPath(harvestedName, idx)
        }
      } else {
        result = extractNameFromFile(harvestedName)
      }
      return result
    }

    private fun extractNameFromPath(path: String, idxOfLastSeparator: Int) = with(path) {
      when {
        endsWith(JAVA_FILE_EXT) -> substring(idxOfLastSeparator + 1, this.length - JAVA_FILE_EXT.length)
        endsWith(GROOVY_FILE_EXT) -> substring(idxOfLastSeparator + 1, this.length - GROOVY_FILE_EXT.length)
        endsWith(KOTLIN_FILE_EXT) -> substring(idxOfLastSeparator + 1, this.length - KOTLIN_FILE_EXT.length)
        else -> substring(idxOfLastSeparator + 1)
      }
    }

    private fun extractNameFromFile(name: String) = with(name) {
      when {
        endsWith(JAVA_FILE_EXT) -> substring(0, this.length - JAVA_FILE_EXT.length)
        endsWith(GROOVY_FILE_EXT) -> substring(0, this.length - GROOVY_FILE_EXT.length)
        endsWith(KOTLIN_FILE_EXT) -> substring(0, this.length - KOTLIN_FILE_EXT.length)
        length != (lastIndexOf(".") + 1) -> substring(lastIndexOf(",") + 1)
        else -> ""
      }
    }

    fun calculateMinimumPatternIndex(antLikePattern: String): Int {
      val idxOfStar = antLikePattern.indexOf("*")
      val idxOfQuestionMark = antLikePattern.indexOf("?")
      return if (-1 < idxOfStar) {
        if (-1 < idxOfQuestionMark) {
          idxOfStar.coerceAtLeast(idxOfQuestionMark)
        } else {
          idxOfStar
        }
      } else {
        if (-1 < idxOfQuestionMark) {
          idxOfQuestionMark
        } else {
          -1
        }
      }
    }

    /**
     * Prepares an id name from a full path or fully qualified file, by
     * replacing various chars with '_'.
     *
     * @param harvestedName string to get ID from
     * @return id name for given harvestedName
     */
    fun generateIdName(harvestedName: String): String = harvestedName.replace("/|\\.|\\\\", "_")

    /**
     * Checks whether the given parameter seems to start with an absolute path
     * fragment according to the current file system.
     *
     * @param fragment fragment to check for absolute path
     * @return true is fragment is absolute path
     */
    fun isAbsolutePathFragment(fragment: String): Boolean = File(fragment).isAbsolute

    /**
     * Whether the fragment hints to a directory structure, supporting Windows
     * or *nix file systems.
     *
     * @param fragment to check
     * @return true if given fragment describes directory structure
     */
    fun hasDirectoryStructure(fragment: String): Boolean = (-1 < fragment.indexOf("/")) ||
      (-1 < fragment.indexOf("\\"))

    /**
     * Extracts from the filename what could serve as extension.
     *
     * @param filename filename to get extension from
     * @return extension from file, or empty if there's no extension.
     */
    fun extractExtension(filename: String): String = with(filename) {
      val idxOfDot = lastIndexOf(".")
      if (-1 < idxOfDot) substring(idxOfDot) else ""
    }

    fun removePrefixDirectory(projectBaseDir: File, fileArgument: String): String = with(fileArgument) {
      val prefix = projectBaseDir.absolutePath + File.separator
      if (startsWith(prefix)) substring(prefix.length) else this
    }
  }

  private constructor() //utils class
}