import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.util.capitalizeDecapitalize.capitalizeAsciiOnly
import java.util.*

plugins {
  // realization
  kotlin("jvm")
  id("java-gradle-plugin")

  // quality
  jacoco
  id("org.unbroken-dome.test-sets")
  id("pl.droidsonroids.jacoco.testkit")
  id("io.gitlab.arturbosch.detekt")
  id("org.sonarqube")

  // documentation
  id("org.jetbrains.dokka")
  id("org.asciidoctor.jvm.convert")

  // publishing
  `maven-publish`
  id("com.gradle.plugin-publish")
  id("com.github.ben-manes.versions")

  idea
}

group = "de.qualersoft"
testSets {
  "funcTest" {
    description = "Runs the functional tests"
  }
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation(kotlin("reflect"))

  implementation(group = "org.robotframework", name = "robotframework", version = "4.0.1")

  testImplementation(group = "org.junit.jupiter", name = "junit-jupiter", version = "5.6.2")
  testImplementation(kotlin("test-junit5"))

  val kotestVer = "4.4.3"
  testImplementation(group = "io.kotest", name = "kotest-runner-junit5", version = kotestVer)
  testImplementation(group = "io.kotest", name = "kotest-assertions-core-jvm", version = kotestVer)

  testRuntimeOnly(kotlin("script-runtime"))
}

jacoco {
  toolVersion = "0.8.7"
}

gradlePlugin {
  plugins {
    create("robotframework") {
      id = "de.qualersoft.robotframework"
      implementationClass = "de.qualersoft.robotframework.gradleplugin.RobotFrameworkPlugin"
      displayName = "robot framework gradle plugin"
      description = "Plugin to integrate robot framework into gradle."
    }
  }
  testSourceSets(*sourceSets.filter { it.name.contains("test", true) }.toTypedArray())
}

pluginBundle {
  website = "https://github.com/qualersoft/robotframework-gradle-plugin"
  vcsUrl = "https://github.com/qualersoft/robotframework-gradle-plugin"
  tags = listOf("robotframework", "test", "integration test", "e2e testing")
}

if (project.version.toString().endsWith("-SNAPSHOT", true)) {
  status = "snapshot"
}

detekt {
  allRules = false
  buildUponDefaultConfig = true
  config = files("$projectDir/detekt.yml")
  input = files("src/main/kotlin")

  reports {
    html.enabled = true
    xml.enabled = true
    txt.enabled = false
  }
}

sonarqube {
  properties {
    property("sonar.projectName", project.name)
    property("sonar.version", project.version)
    property("sonar.projectKey", "qualersoft_robotframework-gradle-plugin")
  }
}

// Setup functional test sets
listOf("groovy", "kotlin").forEach {
  val testName = it.capitalizeAsciiOnly()
  val theTest = tasks.register<Test>("functional${testName}Test") {
    configureDefaultFuncTest(this)
    useJUnitPlatform {
      includeTags.add(it)
    }
    mustRunAfter(project.tasks.test)
  }

  tasks.check {
    dependsOn(theTest)
  }
  jacocoTestKit.applyTo("funcTestRuntimeOnly", theTest as TaskProvider<Task>)
  project.tasks.register<JacocoReport>("jacoco${testName}Report") {
    group = "verification"
    additionalClassDirs(sourceSets.main.get().output.classesDirs)
    additionalSourceDirs(sourceSets.main.get().allSource.sourceDirectories)
    val jacocoExt = theTest.get().extensions.getByName<JacocoTaskExtension>("jacoco")
    executionData(jacocoExt.destinationFile)
    mustRunAfter(theTest)
  }
}

tasks {

  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
      jvmTarget = JavaVersion.VERSION_11.toString()
    }
  }

  validatePlugins {
    enableStricterValidation.set(true)
  }

  this.detekt {
    // Target version of the generated JVM bytecode. It is used for type resolution.
    this.jvmTarget = JavaVersion.VERSION_11.toString()
  }

  withType<Test> {
    useJUnitPlatform()

    testLogging {
      events = mutableSetOf(TestLogEvent.FAILED)
      exceptionFormat = TestExceptionFormat.FULL
    }

    addTestListener(object : TestListener {
      override fun beforeSuite(suite: TestDescriptor) {}
      override fun beforeTest(testDescriptor: TestDescriptor) {}
      override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}
      override fun afterSuite(suite: TestDescriptor, result: TestResult) {
        if (null == suite.parent) { // root suite
          logger.lifecycle("----")
          logger.lifecycle("Test result: ${result.resultType}")
          logger.lifecycle(
            "Test summary: ${result.testCount} tests, " +
              "${result.successfulTestCount} succeeded, " +
              "${result.failedTestCount} failed, " +
              "${result.skippedTestCount} skipped"
          )
        }
      }
    })
  }

  create<JacocoReport>("jacocoMergedReport") {
    description = "Create the final reports of the merged execution data"
    group = test.get().group
    sourceDirectories.from(sourceSets.main.get().allSource.srcDirs)
    classDirectories.from(sourceSets.main.get().output.classesDirs)
    withType<Test>().map { executionData(it) }
  }

  withType<JacocoReport> {
    reports {
      xml.required.set(true)
      html.required.set(true)
      csv.required.set(false)
    }
  }

  dokkaJavadoc.configure { 
    outputDirectory.set(javadoc.get().destinationDir)
  }
  javadoc {
    dependsOn(dokkaJavadoc)
  }
}

publishing {
  repositories {
    maven {
      name = "GitHubPackages"
      url = uri("https://maven.pkg.github.com/qualersoft/robotframework-gradle-plugin")
      credentials {
        username = project.findProperty("publish.gh.mathze.gpr.usr") as String? ?: System.getenv("USERNAME")
        password = project.findProperty("publish.gh.mathze.gpr.key") as String? ?: System.getenv("TOKEN")
      }
    }
  }
}

java {
  targetCompatibility = JavaVersion.VERSION_11
  withSourcesJar()
  withJavadocJar()
}

tasks.register("updateVersion") {
  description = """ONLY FOR CI/CD purposes!
    |
    |This task is meant to be used by CI/CD to generate new release versions.
    |Prerequists: a `gradle.properties` next to this build-script must exist.
    |   version must follow semver-schema (<number>.<number.<number>*)
    |Usage:
    |  > ./gradlew updateVersion -PnewVersion="the new version"
  """.trimMargin()

  doLast {
    var newVersion = project.findProperty("newVersion") as String?
      ?: throw IllegalArgumentException(
        "No `newVersion` specified!" +
          " Usage: ./gradlew updateVersion -PnewVersion=<version>"
      )

    if (newVersion.contains("snapshot", true)) {
      val props = Properties()
      props.load(getGradlePropsFile().inputStream())
      val currVersion = (props["version"] as String?)!!.split('.').toMutableList()
      val next = currVersion.last()
        .replace(Regex("[^\\d]+"), "").toInt() + 1
      currVersion[currVersion.lastIndex] = "$next-SNAPSHOT"
      newVersion = currVersion.joinToString(".")
    }

    persistVersion(newVersion)
  }
}

fun getGradlePropsFile(): File {
  val propsFile = files("./gradle.properties").singleFile
  if (!propsFile.exists()) {
    val msg = "This task requires version to be stored in gradle.properties file, which does not exist!"
    throw UnsupportedOperationException(msg)
  }
  return propsFile
}

fun persistVersion(newVersion: String) {
  val propsFile = getGradlePropsFile()
  val props = Properties()
  props.load(propsFile.inputStream())
  props.setProperty("version", newVersion)
  props.store(propsFile.outputStream(), null)
}

fun configureDefaultFuncTest(test: Test) {
  test.group = "verification"
  val funcTestSourceSet = sourceSets.named("funcTest").get()
  test.testClassesDirs = funcTestSourceSet.output.classesDirs
  test.classpath = funcTestSourceSet.runtimeClasspath

  // Workaround on gradle/jacoco keeping *.exec file locked
  if (org.apache.tools.ant.taskdefs.condition.Os.isFamily(org.apache.tools.ant.taskdefs.condition.Os.FAMILY_WINDOWS)) {
    fun File.isLocked() = !renameTo(this)
    val waitUntilJacocoTestExecIsUnlocked = Action<Task> {
      val jacocoTestExec = checkNotNull(extensions.getByType(JacocoTaskExtension::class).destinationFile)
      val waitMillis = 100L
      var tries = 0
      while (jacocoTestExec.isLocked() && (tries++ < 100)) {
        logger.info("Waiting $waitMillis ms (${jacocoTestExec.name} is locked)...")
        Thread.sleep(waitMillis)
      }
      logger.info("Done waiting (${jacocoTestExec.name} is unlocked).")
    }
    test.doLast(waitUntilJacocoTestExecIsUnlocked)
  }
}