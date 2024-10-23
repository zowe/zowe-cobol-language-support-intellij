/*
 * Copyright (c) 2024 IBA Group.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   IBA Group
 *   Zowe Community
 */

import org.jetbrains.intellij.platform.gradle.TestFrameworkType

fun properties(key: String) = providers.gradleProperty(key)

plugins {
  java
  kotlin("jvm") version "1.9.22"
  id("org.jetbrains.intellij.platform") version "2.1.0"
  id("org.jetbrains.kotlinx.kover") version "0.8.3"
}

group = properties("pluginGroup").get()
version = properties("pluginVersion").get()
val gsonVersion = "2.11.0"
val kotestVersion = "5.9.1"
val mockkVersion = "1.13.13"
val junitVersion = "1.11.3"
val lsp4ijVersion = "0.7.0"

repositories {
  mavenCentral()
  intellijPlatform {
    defaultRepositories()
    jetbrainsRuntime()
  }
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
  jvmToolchain {
    languageVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_17.toString()))
  }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
intellijPlatform {
  pluginConfiguration {
    version = properties("platformVersion").get()
    ideaVersion {
      sinceBuild = properties("pluginSinceBuild").get()
      untilBuild = provider { null }
    }
  }
}

dependencies {
  // ===== Runtime env setup ===
  // IntelliJ
  intellijPlatform {
    intellijIdeaCommunity(properties("platformVersion").get(), useInstaller = false)
    jetbrainsRuntime()
    instrumentationTools()
    bundledPlugin("org.jetbrains.plugins.textmate")
////  pluginsRepositories {
////    custom("https://plugins.jetbrains.com/plugins/nightly/23257")
////  }
    plugin("com.redhat.devtools.lsp4ij:$lsp4ijVersion")
    testFramework(TestFrameworkType.Plugin.Java)
  }
  // Gson
  implementation("com.google.code.gson:gson:$gsonVersion")
  // ===== Test env setup =====
  // Kotest
  testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
  testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
  // MockK
  testImplementation("io.mockk:mockk:$mockkVersion")
  // JUnit Platform (needed for Kotest)
  testImplementation("org.junit.platform:junit-platform-launcher:$junitVersion")
  // ==========================

}

tasks {
  test {
    useJUnitPlatform()
    testLogging {
      events("passed", "skipped", "failed")
    }
    finalizedBy("koverHtmlReport")
  }
}
