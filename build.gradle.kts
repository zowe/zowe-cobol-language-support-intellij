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
 *   Uladzislau Kalesnikau
 */

import org.jetbrains.intellij.platform.gradle.TestFrameworkType

//!IMPORTANT!: to refer "libs", use ./gradle/libs.versions.toml

fun properties(key: String) = providers.gradleProperty(key)

// https://github.com/kotest/kotest-intellij-plugin/blob/master/build.gradle.kts
data class PluginDescriptor(
  val jvmTargetVersion: JavaVersion, // the Java version to use during the plugin build
  val since: String, // earliest version string this is compatible with
  val getUntil: () -> Provider<String>, // latest version string this is compatible with, can be wildcard like 202.*
  // https://github.com/JetBrains/gradle-intellij-plugin#intellij-platform-properties
  val sdkVersion: String, // the version string passed to the intellij sdk gradle plugin
  val postfix: String // used as the indicator to depend on a specific Zowe Explorer version
)

val plugins = listOf(
  PluginDescriptor(
    jvmTargetVersion = JavaVersion.VERSION_17,
    since = properties("pluginSinceBuild").get(),
    getUntil = { provider { "232.*" } },
    sdkVersion = "2023.2",
    postfix = "231"
  ),
  PluginDescriptor(
    jvmTargetVersion = JavaVersion.VERSION_17,
    since = "233.11799",
    getUntil = { provider { "241.*" } },
    sdkVersion = "2023.3",
    postfix = "233"
  ),
  PluginDescriptor(
    jvmTargetVersion = JavaVersion.VERSION_21,
    since = "242.20224",
    getUntil = { provider { "242.*" } },
    sdkVersion = "2024.2",
    postfix = "242"
  ),
  PluginDescriptor(
    jvmTargetVersion = JavaVersion.VERSION_21,
    since = "243.12818",
    getUntil = { provider { null } },
    sdkVersion = "2024.3",
    postfix = "243"
  )
)
val postfix = System.getenv("ZOWE_EXPLORER_POSTFIX") ?: "231"
val descriptor = plugins.first { it.postfix == postfix }

plugins {
  alias(libs.plugins.gradle) // IntelliJ Platform Gradle Plugin
  alias(libs.plugins.kotlinJvm)
  alias(libs.plugins.kover)
  java
}

group = properties("pluginGroup").get()
version = properties("pluginVersion").get()
val lsp4ijVersion = "0.11.0"
val zoweExplorerVersion = "2.2.0-rc.1-$postfix"

repositories {
  mavenCentral()
  intellijPlatform {
    defaultRepositories()
    jetbrainsRuntime()
  }
}

java {
  sourceCompatibility = descriptor.jvmTargetVersion
  targetCompatibility = descriptor.jvmTargetVersion
}

kotlin {
  jvmToolchain {
    jvmToolchain(JavaLanguageVersion.of(descriptor.jvmTargetVersion.toString()).asInt())
  }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
intellijPlatform {
  pluginConfiguration {
    version = "${properties("pluginVersion").get()}-${descriptor.since.substringBefore(".")}"
    ideaVersion {
      sinceBuild = descriptor.since
      untilBuild = descriptor.getUntil()
    }
  }
}

dependencies {
  // ===== Runtime env setup ===
  // IntelliJ
  intellijPlatform {
    intellijIdeaCommunity(descriptor.sdkVersion, useInstaller = false)
    jetbrainsRuntime()
    instrumentationTools()
    bundledPlugin("org.jetbrains.plugins.textmate")
//    pluginsRepositories {
//      custom("https://plugins.jetbrains.com/plugins/nightly/23257")
//    }
    plugin("com.redhat.devtools.lsp4ij:$lsp4ijVersion")
//    localPlugin("D:\\IBA\\IJMP\\zowe-explorer-intellij\\build\\distributions\\zowe-explorer-2.2.0-2024.3.zip")
    plugin("zowe-explorer:$zoweExplorerVersion@preview")
    testFramework(TestFrameworkType.Plugin.Java)
  }
  // Gson
  implementation(libs.gson)
  // ===== Test env setup =====
  // Kotest
  testImplementation(libs.kotest.runner.junit5)
  testImplementation(libs.kotest.assertions.core)
  // MockK
  testImplementation(libs.mockk)
  // JUnit Platform (needed for Kotest)
  testImplementation(libs.junit.platform.launcher)
  // ==========================

}

tasks {
  test {
    useJUnitPlatform()
    testLogging {
      events("passed", "skipped", "failed")
    }
    finalizedBy("koverHtmlReport")
//    testLogging.showStandardStreams = true
  }
}
