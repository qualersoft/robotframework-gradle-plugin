robotframework-gradle-plugin
============================

[![GitHub](https://img.shields.io/github/license/qualersoft/robotframework-gradle-plugin)](https://github.com/qualersoft/robotframework-gradle-plugin/blob/master/LICENSE)
[![CodeFactor](https://www.codefactor.io/repository/github/qualersoft/robotframework-gradle-plugin/badge?s=2996b4322bfcdca3d8e6250191d67a1410cf3a16)](https://www.codefactor.io/repository/github/qualersoft/robotframework-gradle-plugin)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fqualersoft%2Frobot-gradle-plugin.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Fqualersoft%2Frobot-gradle-plugin?ref=badge_shield)
[![codecov](https://codecov.io/gh/qualersoft/robotframework-gradle-plugin/branch/master/graph/badge.svg?token=Z5CT2C7LN1)](https://codecov.io/gh/qualersoft/robotframework-gradle-plugin)

Gradle plugin for using the [Robot Framework](https://robotframework.org/).
This project is inspired by the [maven robotframework plugin](https://github.com/robotframework/MavenPlugin).
Its goal is to enable you to use the Robot Framework in a gradle project without the need to install anything extra
(e.g. Robotframework, Jython, etc.).

Quickstart
----------
Just add the following lines to you build.gradle(.kts)
```groovy
import de.qualersoft.robotframework.gradleplugin.tasks

plugins {
  id("de.qualersoft.robotframework") version "<latest>"
}

tasks.register<RunRobotTask>("robotTest") {
  sources = fileTree("src/test") {
    include("**/*.robot")
  }
}
```
_Note_: Don't forget to replace the version `"<latest>"` with the appropriate version :wink:

Assuming you have some robot test suite files somewhere under your `src/test` folder, calling
```shell
./gradlew robotTest
```
starts the test execution.

For further configuration and examples have a look at the [test resources](src/funcTest/resources).

Available tasks*
---------------
For details see [robot framework tasks](http://robotframework.org/robotframework/#built-in-tools)
<dl>
  <dt>RunRobotTask</dt>
  <dd>same as <code>run</code> command</dd>
  <dt>LibdocTask</dt>
  <dd>same as <code>libdoc</code> command</dd>
  <dt>TestdocTask</dt>
  <dd>same as <code>testdoc</code> command</dd>
  <dt>RebotTask</dt>
  <dd style="color:red; font-weight:bold;">TODO</dd>
  <dt>TidyTask</dt>
  <dd>same as <code>tidy</code> command</dd>
</dl>

(*) All task implementations are in package `de.qualersoft.robotframework.gradleplugin.tasks`