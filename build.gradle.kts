import io.gitlab.arturbosch.detekt.internal.configurableFileCollection

plugins {
  kotlin("jvm")
  id("java-gradle-plugin")

  id("org.jetbrains.dokka")

  jacoco
  id("io.gitlab.arturbosch.detekt")
  id("org.sonarqube")

  `maven-publish`
}

group = "de.qualersoft"
version = "0.0.0-SNAPSHOT"

repositories {
  jcenter()
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation(kotlin("reflect"))

  implementation(group = "org.robotframework", name = "robotframework", version = "3.2")

  testImplementation(kotlin("test-junit5"))

  val kotestVersion = "4.0.5"
  testImplementation(group = "io.kotest", name = "kotest-runner-junit5-jvm", version = kotestVersion) // for kotest framework
  testImplementation(group = "io.kotest", name = "kotest-assertions-core-jvm", version = kotestVersion) // for kotest core jvm assertions
  testImplementation(group = "io.kotest", name = "kotest-property-jvm", version = kotestVersion) // for kotest property test
  testRuntimeOnly(kotlin("script-runtime"))
}

gradlePlugin {
  plugins {
    register("robotframework") {
      id = "de.qualersoft.robotframework"
      implementationClass = "de.qualersoft.robotframework.gradleplugin.RobotFrameworkPlugin"
    }
  }
}
tasks.validatePlugins {
  enableStricterValidation.set(true)
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

tasks.detekt {
  // Target version of the generated JVM bytecode. It is used for type resolution.
  this.jvmTarget = JavaVersion.VERSION_11.toString()
}

sonarqube {
  properties {
    property("sonar.projectName", project.name)
    property("sonar.projectKey", project.name)
    property("sonar.version", project.version)
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
  finalizedBy(tasks.jacocoTestReport)
}

jacoco {
  toolVersion = "0.8.5"
}

tasks.jacocoTestReport {
  dependsOn(tasks.test)
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