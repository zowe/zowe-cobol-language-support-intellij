/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright IBA Group 2024
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = providers.gradleProperty(key)

plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "1.9.21"
  id("org.jetbrains.intellij") version "1.16.1"
}

group = properties("pluginGroup").get()
version = properties("pluginVersion").get()

repositories {
  mavenCentral()
  maven {
    url = uri("https://jitpack.io") // lsp4intellij
  }
}

dependencies {
  // TODO: update the dependency to fix the issues asap
  // CVE-2023-2976 - no impact on the project (25.04.2024)
  // CVE-2020-8908 - no impact on the project (25.04.2024)
  implementation("com.github.ballerina-platform:lsp4intellij:0.96.0")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
  version.set(properties("platformVersion").get())
  plugins.set(listOf("org.jetbrains.plugins.textmate"))
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
}
