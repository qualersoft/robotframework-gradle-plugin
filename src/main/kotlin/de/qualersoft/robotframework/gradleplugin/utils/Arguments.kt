package de.qualersoft.robotframework.gradleplugin.utils

import java.io.File

@Suppress("TooManyFunctions")
open class Arguments {
  private val args: MutableList<String> = arrayListOf()

  fun addOptionalFile(file: File?, flag: String) {
    if(null != file) {
      add(flag, file.path)
    }
  }

  fun addFileToArguments(file: File?, flag: String) {
    if (null == file) {
      add(flag, "NONE")
    } else if (isFileValid(file)) {
      add(flag, file.path)
    }
  }

  private fun isFileValid(file: File): Boolean = file.path != ""

  fun addNonEmptyStringToArguments(variableToAdd: String?, flag: String) {
    if (!variableToAdd.isNullOrEmpty()) {
      addStringToArguments(variableToAdd, flag)
    }
  }

  fun addMapToArguments(valToAdd: Map<String, String>, flag: String) {
    for((key, value) in valToAdd) {
      addStringToArguments("$key:$value", flag)
    }
  }

  fun addFlagToArguments(flag: Boolean?, argument: String) {
    if ((null != flag) && flag) {
      add(argument)
    }
  }

  fun addStringToArguments(variableToAdd: String?, flag: String) {
    if (null != variableToAdd) {
      add(flag, variableToAdd)
    }
  }

  fun addListToArguments(variablesToAdd: String?, flag: String) {
    if (null != variablesToAdd) {
      addListToArguments(variablesToAdd.split(","), flag)
    }
  }

  fun addListToArguments(variablesToAdd: List<String?>?, flag: String) {
    if (null != variablesToAdd) {
      for (variableToAdd in variablesToAdd) {
        if (!variableToAdd.isNullOrEmpty()) {
          add(flag, variableToAdd)
        }
      }
    }
  }

  fun addFileListToArguments(variablesToAdd: List<File>?, flag: String) {
    if (null != variablesToAdd) {
      for (variableToAdd in variablesToAdd) {
        addFileToArguments(variableToAdd, flag)
      }
    }
  }

  fun add(flag: String, vararg values: String) {
    if (values.isNullOrEmpty()) {
      args.add(flag)
    } else {
      values.forEach {
        addArgs(arrayOf(flag, it))
      }
    }
  }

  fun addArgs(args: Array<String>) = this.args.addAll(args)

  fun toArray(): Array<String> = args.toTypedArray()
}