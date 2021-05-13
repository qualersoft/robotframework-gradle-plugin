import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.util.Properties

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
  // required by detekt unless upgraded to 1.17
  jcenter()
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

jacocoTestKit {
  applyTo("funcTestRuntimeOnly", tasks.named("funcTest"))
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

tasks.validatePlugins {
  enableStricterValidation.set(true)
}

tasks.detekt {
  // Target version of the generated JVM bytecode. It is used for type resolution.
  this.jvmTarget = JavaVersion.VERSION_11.toString()
}

tasks.withType<Test> {
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

  finalizedBy(
    when (name) {
      tasks.test.name -> tasks.jacocoTestReport
      "funcTest" -> tasks.named("jacocoFuncTestReport")
      else -> throw IllegalArgumentException("Unknown test task '$name' don't know which jacoco report to apply")
    }
  )
}

tasks.named<Test>("funcTest") {
  mustRunAfter(tasks.generateJacocoTestKitProperties)
  // Workaround for https://github.com/koral--/jacoco-gradle-testkit-plugin/issues/9
  if (Os.isFamily(Os.FAMILY_WINDOWS)) {
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
    doLast(waitUntilJacocoTestExecIsUnlocked)
  }
}

val jacocoMerge = tasks.create<JacocoMerge>("jacocoMerge") {
  description = "Create the merged execution data of all test runs"
  val reportsTasks = tasks.withType<JacocoReport>().filter { it.name != "jacocoMergedReport" }.toTypedArray()
  executionData(*reportsTasks.flatMap { it.executionData.files }.toTypedArray())
  mustRunAfter(reportsTasks)
}

tasks.create<JacocoReport>("jacocoMergedReport") {
  description = "Create the final reports of the merged execution data"
  group = tasks.test.get().group
  dependsOn(jacocoMerge)
  sourceDirectories.from(sourceSets.main.get().allSource.srcDirs)
  classDirectories.from(sourceSets.main.get().output.classesDirs)
  executionData(tasks.getByName<JacocoMerge>("jacocoMerge").destinationFile)
}

tasks.withType<JacocoReport> {
  reports {
    xml.isEnabled = true
    html.isEnabled = true
    csv.isEnabled = false
  }
}

tasks.dokkaHtml {
  outputDirectory.set(file("$buildDir/javadoc"))
}

val dokkaJar by tasks.creating(Jar::class) {
  group = JavaBasePlugin.DOCUMENTATION_GROUP
  description = "Assembles Kotlin docs with Dokka"
  archiveClassifier.set("javadoc")
  from(tasks.dokkaHtml)
}

pluginBundle {
  website = "https://github.com/qualersoft/robotframework-gradle-plugin"
  vcsUrl = "https://github.com/qualersoft/robotframework-gradle-plugin"
  tags = listOf("robotframework", "test", "integration test", "e2e testing")
}

publishing {
  publications {
    create<MavenPublication>("pluginMaven") {
      // customize main publications here
      artifact(tasks.kotlinSourcesJar)
      artifact(dokkaJar)
    }
  }

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

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions {
  jvmTarget = JavaVersion.VERSION_11.toString()
}
val compileTestKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
  jvmTarget = JavaVersion.VERSION_11.toString()
}

java {
  targetCompatibility = JavaVersion.VERSION_11
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
