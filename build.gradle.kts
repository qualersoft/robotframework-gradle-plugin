import io.gitlab.arturbosch.detekt.internal.configurableFileCollection

plugins {
  kotlin("jvm")
  id("java-gradle-plugin")

  id("org.jetbrains.dokka")

  id("org.unbroken-dome.test-sets") version "3.0.1"
  jacoco
  // workaround to integrate jacoco coverage into integration tests. (See https://github.com/gradle/gradle/issues/1465)
  id("pl.droidsonroids.jacoco.testkit") version "1.0.7"

  id("io.gitlab.arturbosch.detekt")
  id("org.sonarqube")

  `maven-publish`
}

group = "de.qualersoft"
version = "0.0.1-SNAPSHOT"

testSets {
  "funcTest" {
    description = "Runs the functional tests"
    extendsFrom("unitTest")
  }
}
jacocoTestKit {
  applyTo("testRuntime", tasks.named("funcTest"))
}

gradlePlugin {
  plugins {
    register("robotframework") {
      id = "de.qualersoft.robotframework"
      implementationClass = "de.qualersoft.robotframework.gradleplugin.RobotFrameworkPlugin"
    }
  }
  testSourceSets(*sourceSets.filter { it.name.contains("test") }.toTypedArray())
}

detekt {
  failFast = true
  config = files("$projectDir/detekt.yml")
  input = project.configurableFileCollection().from("src/main/kotlin")

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
  toolVersion = "0.8.5"
}

repositories {
  jcenter()
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation(kotlin("reflect"))

  implementation(group = "org.robotframework", name = "robotframework", version = "3.2")

  testImplementation(group="org.junit.jupiter", name="junit-jupiter", version="5.6.2")
  testImplementation(kotlin("test-junit5"))

  testImplementation(group = "io.kotest", name="kotest-runner-junit5", version = "4.0.5")
  testImplementation(group="io.kotest", name="kotest-assertions-core-jvm", version="4.0.5")

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
  finalizedBy(when (name) {
    "test" -> tasks.jacocoTestReport
    "funcTest" -> tasks.getByName("jacocoFuncTestReport")
    else -> throw IllegalArgumentException("Unknown test type '$name'")
  })
}

tasks.create<JacocoMerge>("jacocoMerge") {
  executionData(tasks.withType<Test>())
}

tasks.create<JacocoReport>("reportMerge") {
  sourceDirectories.from(sourceSets.main.get().allSource.srcDirs)
  classDirectories.from(sourceSets.main.get().output.classesDirs)
  executionData(tasks.getByName<JacocoMerge>("jacocoMerge").destinationFile)
}

tasks.withType<JacocoReport>() {
  reports {
    xml.isEnabled = true
    html.isEnabled = true
    csv.isEnabled = false
  }
}

tasks.dokka {
  outputFormat = "html"
  outputDirectory = "$buildDir/javadoc"
}

val dokkaJar by tasks.creating(Jar::class) {
  group = JavaBasePlugin.DOCUMENTATION_GROUP
  description = "Assembles Kotlin docs with Dokka"
  archiveClassifier.set("javadoc")
  from(tasks.dokka)
}

// Create sources Jar from main kotlin sources
val sourcesJar by tasks.creating(Jar::class) {
  group = JavaBasePlugin.DOCUMENTATION_GROUP
  description = "Assembles sources JAR"
  archiveClassifier.set("sources")
  from(project.the<SourceSetContainer>()["main"].allSource)
}

publishing {
  publications {
    create<MavenPublication>("default") {
      from(components["java"])
      artifact(sourcesJar)
      artifact(dokkaJar)
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