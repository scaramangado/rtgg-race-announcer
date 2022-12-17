import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  java
  id("org.springframework.boot") version "2.7.6"
  id("io.spring.dependency-management") version "1.1.0"
  kotlin("jvm") version "1.7.21"
  kotlin("plugin.spring") version "1.7.21"
}

group = "de.scaramangado"
version = "0.0.1-SNAPSHOT"

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<Jar> {
  archiveBaseName.set("announcer")
  archiveVersion.set("")
}

repositories {
  mavenCentral()
  jcenter()
}

dependencies {

  implementation("org.springframework.boot:spring-boot-starter")

  implementation("org.glassfish.tyrus.bundles:tyrus-standalone-client:1.20")
  implementation("org.springframework:spring-websocket")
  implementation("org.springframework:spring-messaging")

  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  implementation("com.google.code.gson:gson")

  implementation("net.dv8tion:JDA:4.2.0_227") {
    exclude(module = "opus-java")
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "17"
  }
}

tasks.withType<Wrapper> {
  gradleVersion = "7.6"
}
