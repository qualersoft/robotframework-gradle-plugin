package de.qualersoft.robotframework.gradleplugin.utils

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.collections.shouldHaveElementAt
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.io.File

class ArgumentsTest : AnnotationSpec() {

  private lateinit var sut: Arguments

  @BeforeEach
  fun setupTest() {
    sut = Arguments()
  }

  @Test
  fun givenNewArgumentsThenResultIsEmpty() {
    sut.shouldBeEmpty()
  }

  @Test
  fun whenAddingAnOptionalFileOfNullThenResultIsEmpty() {
    sut.addOptionalFile(null, "a")
    sut.shouldBeEmpty()
  }

  @Test
  fun whenAddingAnOptionalNonNullFileThenResultIsNotEmpty() {
    sut.addOptionalFile(File("./test"), "a")
    assertSoftly {
      sut.shouldNotBeEmpty()
      val arr = sut.toArray()
      arr.shouldHaveSize(2)
      arr.shouldHaveElementAt(0, "a")
      arr.shouldHaveElementAt(1, File("./test").path)
    }
  }

  @Test
  fun whenAddingNullFileThenItsConvertedToNone() {
    sut.addFileToArguments(null, "f")
    assertSoftly {
      sut.shouldNotBeEmpty()
      val arr = sut.toArray()
      arr.shouldHaveSize(2)
      arr.shouldHaveElementAt(0, "f")
      arr.shouldHaveElementAt(1, "NONE")
    }
  }

  @Test
  fun whenAddingNonEmptyFileThenItWillBeInResult() {
    sut.addFileToArguments(File("./test"), "f")
    assertSoftly {
      sut.shouldNotBeEmpty()
      val arr = sut.toArray()
      arr.shouldHaveSize(2)
      arr.shouldHaveElementAt(0, "f")
      arr.shouldHaveElementAt(1, File("./test").path)
    }
  }

  @Test
  fun whenAddingEmptyFileThenItsNotInResult() {
    sut.addFileToArguments(File(""), "f")
    sut.shouldBeEmpty()
  }

  @Test
  fun whenAddingNullStringThenItsNotInResult() {
    sut.addNonEmptyStringToArguments(null, "s")
    sut.shouldBeEmpty()
  }

  @Test
  fun whenAddingEmptyStringThenItsNotInResult() {
    sut.addNonEmptyStringToArguments("", "s")
    sut.shouldBeEmpty()
  }

  @Test
  fun whenAddingNonEmptyStringThenItsInResult() {
    sut.addNonEmptyStringToArguments("notEmpty", "s")
    assertSoftly {
      sut.shouldNotBeEmpty()
      val arr = sut.toArray()
      arr.shouldHaveSize(2)
      arr.shouldHaveElementAt(0, "s")
      arr.shouldHaveElementAt(1, "notEmpty")
    }
  }

  @Test
  fun whenAddingEmptyMapThenItsNotInResult() {
    sut.addMapToArguments(mapOf(), "m")
    sut.shouldBeEmpty()
  }

  @Test
  fun whenAddingMapThenItsInResult() {
    sut.addMapToArguments(mapOf("key" to "val"), "m")
    assertSoftly {
      sut.shouldNotBeEmpty()
      val arr = sut.toArray()
      arr.shouldHaveSize(2)
      arr.shouldHaveElementAt(0, "m")
      arr.shouldHaveElementAt(1, "key:val")
    }
  }

  @Test
  fun whenAddingMultiMapThenItsInResult() {
    sut.addMapToArguments(mapOf("key1" to "val1", "key2" to "val2"), "m")
    assertSoftly {
      sut.shouldNotBeEmpty()
      val arr = sut.toArray()
      arr.shouldHaveSize(4)
      arr.shouldHaveElementAt(0, "m")
      arr.shouldHaveElementAt(1, "key1:val1")
      arr.shouldHaveElementAt(2, "m")
      arr.shouldHaveElementAt(3, "key2:val2")
    }
  }

  @Test
  fun whenAddingNullFlagThenItsNotInResult() {
    sut.addFlagToArguments(null, "b")
    sut.shouldBeEmpty()
  }

  @Test
  fun whenAddingFalseFlagThenItsNotInResult() {
    sut.addFlagToArguments(false, "b")
    sut.shouldBeEmpty()
  }

  @Test
  fun whenAddingTrueFlagThenItsInResult() {
    sut.addFlagToArguments(true, "b")
    assertSoftly {
      sut.shouldNotBeEmpty()
      val arr = sut.toArray()
      arr.shouldHaveSize(1)
      arr.shouldHaveElementAt(0, "b")
    }
  }

  @Test
  fun whenAddingOptionalNullStringThenItsNotInResult() {
    sut.addStringToArguments(null, "s")
    sut.shouldBeEmpty()
  }

  @Test
  fun whenAddingOptionalEmptyStringThenItsInResult() {
    sut.addStringToArguments("", "s")
    assertSoftly {
      sut.shouldNotBeEmpty()
      val arr = sut.toArray()
      arr.shouldHaveSize(2)
      arr.shouldHaveElementAt(0, "s")
      arr.shouldHaveElementAt(1, "")
    }
  }

  @Test
  fun whenAddingOptionalNonEmptyStringThenItsInResult() {
    sut.addStringToArguments("NotEmpty", "s")
    assertSoftly {
      sut.shouldNotBeEmpty()
      val arr = sut.toArray()
      arr.shouldHaveSize(2)
      arr.shouldHaveElementAt(0, "s")
      arr.shouldHaveElementAt(1, "NotEmpty")
    }
  }

  @Test
  fun whenAddNullStringListThenItsNotInResult() {
    sut.addListToArguments(null as String?, "s")
    sut.shouldBeEmpty()
  }

  @Test
  fun whenAddStringListThenItsInResult() {
    sut.addListToArguments("aString", "s")
    assertSoftly {
      sut.shouldNotBeEmpty()
      val arr = sut.toArray()
      arr.shouldHaveSize(2)
      arr.shouldHaveElementAt(0, "s")
      arr.shouldHaveElementAt(1, "aString")
    }
  }

  @Test
  fun whenAddingMultiStringListThenEachIsInResult() {
    sut.addListToArguments("str1, str2", "s")
    assertSoftly {
      sut.shouldNotBeEmpty()
      val arr = sut.toArray()
      arr.shouldHaveSize(4)
      arr.shouldHaveElementAt(0, "s")
      arr.shouldHaveElementAt(1, "str1")
      arr.shouldHaveElementAt(2, "s")
      arr.shouldHaveElementAt(3, "str2")
    }
  }

  @Test
  fun whenAddingNullListThenItsNotInResult() {
    sut.addListToArguments(null as List<String?>?, "ls")
    sut.shouldBeEmpty()
  }

  @Test
  fun whenAddingEmptyListThenItsNotInResult() {
    sut.addListToArguments(listOf(), "ls")
    sut.shouldBeEmpty()
  }

  @Test
  fun whenAddingListWithNullThenItsNotInResult() {
    sut.addListToArguments(listOf<String?>(null), "ls")
    sut.shouldBeEmpty()
  }

  @Test
  fun whenAddingListWithEmptyThenItsNotInResult() {
    sut.addListToArguments(listOf(""), "ls")
    sut.shouldBeEmpty()
  }

  @Test
  fun whenAddingListThenItsInResult() {
    sut.addListToArguments(listOf("str"), "ls")
    assertSoftly {
      sut.shouldNotBeEmpty()
      val arr = sut.toArray()
      arr.shouldHaveSize(2)
      arr.shouldHaveElementAt(0, "ls")
      arr.shouldHaveElementAt(1, "str")
    }
  }

  @Test
  fun whenAddingListWithMoreElemsThenEachIsInResult() {
    sut.addListToArguments(listOf("str1", "str2"), "ls")
    assertSoftly {
      sut.shouldNotBeEmpty()
      val arr = sut.toArray()
      arr.shouldHaveSize(4)
      arr.shouldHaveElementAt(0, "ls")
      arr.shouldHaveElementAt(1, "str1")
      arr.shouldHaveElementAt(2, "ls")
      arr.shouldHaveElementAt(3, "str2")
    }
  }

  @Test
  fun whenAddingNullFileListThenItsNotInResult() {
    sut.addFileListToArguments(null, "fl")
    sut.shouldBeEmpty()
  }

  @Test
  fun whenAddingEmptyFileListThenItsNotInResult() {
    sut.addFileListToArguments(listOf(), "fl")
    sut.shouldBeEmpty()
  }

  @Test
  fun whenAddingFileListWithEmptyFileThenItsNotInResult() {
    sut.addFileListToArguments(listOf(File("")), "fl")
    sut.shouldBeEmpty()
  }

  @Test
  fun whenAddingFileListWithFileThenItsInResult() {
    sut.addFileListToArguments(listOf(File("./test")), "fl")
    assertSoftly {
      sut.shouldNotBeEmpty()
      val arr = sut.toArray()
      arr.shouldHaveSize(2)
      arr.shouldHaveElementAt(0, "fl")
      arr.shouldHaveElementAt(1, File("./test").path)
    }
  }

  // <editor-fold desc="Helper extensions">
  private fun beEmpty() = object : Matcher<Arguments> {
    override fun test(value: Arguments) = MatcherResult(
      value.toArray().isEmpty(),
      "Arguments $value should be empty",
      "String $value should not be empty"
    )
  }

  private fun Arguments.shouldBeEmpty() = this should beEmpty()
  private fun Arguments.shouldNotBeEmpty() = this shouldNot beEmpty()
  // </editor-fold>
}
