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

  `maven-publish`
  idea
}

group = "de.qualersoft"
version = "0.0.1-SNAPSHOT"

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
    property("sonar.projectKey", project.name)
    property("sonar.version", project.version)
  }
}

jacoco {
  toolVersion = "0.8.6"
}

repositories {
  jcenter()
  mavenCentral()
  maven {
    url = uri("https://nexus.memathze.de/repository/maven-public/")
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

val mavenUsr: String by project
val mavenPwd: String by project

publishing {
  publications {
    create<MavenPublication>("pluginMaven") {
      // customize main publications here
      artifact(sourcesJar)
      artifact(dokkaJar)

    }
  }

  repositories {
    maven {
      name = "Nexus"
      credentials {
        username = mavenUsr
        password = mavenPwd
      }

      url = if ("${project.version}".endsWith("-SNAPSHOT")) {
        uri("https://nexus.memathze.de/repository/maven-snapshots/")
      } else {
        uri("https://nexus.memathze.de/repository/maven-releases/")
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