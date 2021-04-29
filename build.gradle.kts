import java.util.Properties
val repoUsr: String? by project
val repoPwd: String? by project
val repoUrl: String? by project

plugins {
  // realization
  kotlin("jvm")
  id("java-gradle-plugin")

  // quality
  jacoco
  id("org.unbroken-dome.test-sets") version "3.0.1"
  // workaround to integrate jacoco coverage into integration tests. (See https://github.com/gradle/gradle/issues/1465)
  id("pl.droidsonroids.jacoco.testkit") version "1.0.7"
  id("io.gitlab.arturbosch.detekt")
  id("org.sonarqube")

  // documentation
  id("org.jetbrains.dokka")
  id("org.asciidoctor.jvm.convert")

  // publishing
  `maven-publish`
  id("com.gradle.plugin-publish")

  idea
  id("com.github.ben-manes.versions") version "0.38.0"
}

group = "de.qualersoft"

testSets {
  "funcTest" {
    description = "Runs the functional tests"
  }
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

jacoco {
  toolVersion = "0.8.6"
}

repositories {
  jcenter()
  mavenCentral()
  maven {
    url = uri("$repoUrl/maven-public/")
  }
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation(kotlin("reflect"))

  implementation(group = "org.robotframework", name = "robotframework", version = "3.2.2")

  testImplementation(group = "org.junit.jupiter", name = "junit-jupiter", version = "5.6.2")
  testImplementation(kotlin("test-junit5"))

  val kotestVer = "4.4.3"
  testImplementation(group = "io.kotest", name = "kotest-runner-junit5", version = kotestVer)
  testImplementation(group = "io.kotest", name = "kotest-assertions-core-jvm", version = kotestVer)

  testRuntimeOnly(kotlin("script-runtime"))
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
  finalizedBy(
    when (name) {
      "test" -> tasks.jacocoTestReport
      "funcTest" -> tasks.named("jacocoFuncTestReport")
      else -> throw IllegalArgumentException("Unknown test type '$name'")
    }
  )
}

tasks.create<JacocoMerge>("jacocoMerge") {
  val reportsTasks = tasks.withType<JacocoReport>().filter { it.name != "reportMerge" }.toTypedArray()
  executionData(*reportsTasks.map { it.executionData.singleFile }.toTypedArray())
}

tasks.create<JacocoReport>("reportMerge") {
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

// Create sources Jar from main kotlin sources
val sourcesJar by tasks.creating(Jar::class) {
  group = JavaBasePlugin.DOCUMENTATION_GROUP
  description = "Assembles sources JAR"
  archiveClassifier.set("sources")
  from(project.the<SourceSetContainer>()["main"].allSource)
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
      artifact(tasks.jar)
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