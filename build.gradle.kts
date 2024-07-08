/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = providers.gradleProperty(key)

plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "1.9.21"
  id("org.jetbrains.intellij") version "1.16.1"
  id("org.jetbrains.kotlinx.kover") version "0.8.1"
}

group = properties("pluginGroup").get()
version = properties("pluginVersion").get()
val kotestVersion = "5.9.1"
val mockkVersion = "1.13.11"
val junitVersion = ""

repositories {
  mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
  version.set(properties("platformVersion").get())
//  pluginsRepositories {
//    custom("https://plugins.jetbrains.com/plugins/nightly/23257")
//  }
  plugins.set(listOf("org.jetbrains.plugins.textmate", "com.redhat.devtools.lsp4ij:0.0.2"))
}

dependencies {
  // ===== Test env setup =====
  // Kotest
  testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
  testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
  // MockK
  testImplementation("io.mockk:mockk:$mockkVersion")
  // JUnit Platform (needed for Kotest)
  testImplementation("org.junit.platform:junit-platform-launcher:1.10.2")
  // ==========================

}

tasks {
  // Set the JVM compatibility versions
  withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_17.toString()
    targetCompatibility = JavaVersion.VERSION_17.toString()
  }
  withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
  }

  patchPluginXml {
    version.set(properties("pluginVersion").get())
    sinceBuild.set(properties("pluginSinceBuild").get())
    untilBuild.set(properties("pluginUntilBuild").get())
  }

  test {
    useJUnitPlatform()
    testLogging {
      events("passed", "skipped", "failed")
    }
    finalizedBy("koverHtmlReport")
  }
}
