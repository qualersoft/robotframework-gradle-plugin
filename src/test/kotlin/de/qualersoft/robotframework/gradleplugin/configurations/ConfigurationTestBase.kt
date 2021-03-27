package de.qualersoft.robotframework.gradleplugin.configurations

import io.kotest.assertions.show.show
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult

open class ConfigurationTestBase {

  protected fun <C : Collection<String>> haveElementContains(expected: String) = object : Matcher<C> {
    override fun test(value: C) = MatcherResult(
      value.any { it.contains(expected) },
      {
        "Collection should have element which contains ${expected.show().value};" +
          " listing some elements ${value.take(5)}"
      },
      { "Collection should not have element which contains ${expected.show().value}" }
    )
  }
}