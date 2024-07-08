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

package org.zowe.cobol.lsp

import com.intellij.openapi.project.Project
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.eclipse.lsp4j.ConfigurationItem
import org.eclipse.lsp4j.ConfigurationParams
import org.eclipse.lsp4j.MessageParams

class CobolLanguageClientTestSpec : FunSpec({

  context("CobolLanguageClientTestSpec.configuration") {
    afterTest {
      unmockkAll()
      clearAllMocks()
    }

    test("process 'workspace/configuration' for dialect registry section request") {
      var isLogMessageTriggeredCorrectly = false

      val projectMock = mockk<Project>()
      val configurationItemMock = mockk<ConfigurationItem> {
        every { section } returns DIALECT_REGISTRY_SECTION
      }
      val configurationParamsMock = mockk<ConfigurationParams> {
        every { items } returns listOf(configurationItemMock)
      }
      val cobolLanguageClient = spyk(CobolLanguageClient(projectMock))
      every { cobolLanguageClient.logMessage(any<MessageParams>()) } answers {
        if (firstArg<MessageParams>().message.contains(DIALECT_REGISTRY_SECTION)) {
          isLogMessageTriggeredCorrectly = true
        }
      }

      val result = cobolLanguageClient.configuration(configurationParamsMock).join()

      assertSoftly { isLogMessageTriggeredCorrectly shouldBe true }
      assertSoftly { result shouldBeEqual listOf(emptyList<String>()) }
    }

    test("process 'workspace/configuration' for unrecognized request") {
      var isLogMessageTriggeredCorrectly = false

      val someTestSection = "test-unrecognized-section"
      val projectMock = mockk<Project>()
      val configurationItemMock = mockk<ConfigurationItem> {
        every { section } returns someTestSection
        every { scopeUri } returns ""
      }
      val configurationParamsMock = mockk<ConfigurationParams> {
        every { items } returns listOf(configurationItemMock)
      }
      val cobolLanguageClient = spyk(CobolLanguageClient(projectMock))
      every { cobolLanguageClient.logMessage(any<MessageParams>()) } answers {
        if (firstArg<MessageParams>().message.contains(someTestSection)) {
          isLogMessageTriggeredCorrectly = true
        }
      }

      val result = cobolLanguageClient.configuration(configurationParamsMock).join()

      assertSoftly { isLogMessageTriggeredCorrectly shouldBe true }
      assertSoftly { result shouldBeEqual listOf(emptyList<String>()) }
    }

    test("process 'workspace/configuration' for dialects request with scope URI") {
      var isLogMessageTriggeredCorrectly = false

      val projectMock = mockk<Project>()
      val configurationItemMock = mockk<ConfigurationItem> {
        every { scopeUri } returns "test"
        every { section } returns SETTINGS_DIALECT
      }
      val configurationParamsMock = mockk<ConfigurationParams> {
        every { items } returns listOf(configurationItemMock)
      }
      val cobolLanguageClient = spyk(CobolLanguageClient(projectMock))
      every { cobolLanguageClient.logMessage(any<MessageParams>()) } answers {
        if (firstArg<MessageParams>().message.contains(SETTINGS_DIALECT)) {
          isLogMessageTriggeredCorrectly = true
        }
      }

      val result = cobolLanguageClient.configuration(configurationParamsMock).join()

      assertSoftly { isLogMessageTriggeredCorrectly shouldBe true }
      assertSoftly { result shouldBeEqual listOf(emptyList<String>()) }
    }

    test("process 'workspace/configuration' for cpy-manager paths-local request with scope URI") {
      var isLogMessageTriggeredCorrectly = false

      val projectMock = mockk<Project>()
      val configurationItemMock = mockk<ConfigurationItem> {
        every { scopeUri } returns "test"
        every { section } returns SETTINGS_CPY_LOCAL_PATH
      }
      val configurationParamsMock = mockk<ConfigurationParams> {
        every { items } returns listOf(configurationItemMock)
      }
      val cobolLanguageClient = spyk(CobolLanguageClient(projectMock))
      every { cobolLanguageClient.logMessage(any<MessageParams>()) } answers {
        if (firstArg<MessageParams>().message.contains(SETTINGS_CPY_LOCAL_PATH)) {
          isLogMessageTriggeredCorrectly = true
        }
      }

      val result = cobolLanguageClient.configuration(configurationParamsMock).join()

      assertSoftly { isLogMessageTriggeredCorrectly shouldBe true }
      assertSoftly { result shouldBeEqual listOf() }
    }

    test("process 'workspace/configuration' for dialect libs request with scope URI") {
      var isLogMessageTriggeredCorrectly = false

      val projectMock = mockk<Project>()
      val configurationItemMock = mockk<ConfigurationItem> {
        every { scopeUri } returns "test"
        every { section } returns DIALECT_LIBS
      }
      val configurationParamsMock = mockk<ConfigurationParams> {
        every { items } returns listOf(configurationItemMock)
      }
      val cobolLanguageClient = spyk(CobolLanguageClient(projectMock))
      every { cobolLanguageClient.logMessage(any<MessageParams>()) } answers {
        if (firstArg<MessageParams>().message.contains(DIALECT_LIBS)) {
          isLogMessageTriggeredCorrectly = true
        }
      }

      val result = cobolLanguageClient.configuration(configurationParamsMock).join()

      assertSoftly { isLogMessageTriggeredCorrectly shouldBe true }
      assertSoftly { result shouldBeEqual listOf() }
    }

    test("process 'workspace/configuration' for cpy-manager copybook-extensions request with scope URI") {
      var isLogMessageTriggeredCorrectly = false

      val projectMock = mockk<Project>()
      val configurationItemMock = mockk<ConfigurationItem> {
        every { scopeUri } returns "test"
        every { section } returns SETTINGS_CPY_EXTENSIONS
      }
      val configurationParamsMock = mockk<ConfigurationParams> {
        every { items } returns listOf(configurationItemMock)
      }
      val cobolLanguageClient = spyk(CobolLanguageClient(projectMock))
      every { cobolLanguageClient.logMessage(any<MessageParams>()) } answers {
        if (firstArg<MessageParams>().message.contains(SETTINGS_CPY_EXTENSIONS)) {
          isLogMessageTriggeredCorrectly = true
        }
      }

      val result = cobolLanguageClient.configuration(configurationParamsMock).join()

      assertSoftly { isLogMessageTriggeredCorrectly shouldBe true }
      assertSoftly { result shouldBeEqual listOf(listOf(".CPY", ".COPY", ".cpy", ".copy", "")) }
    }

    test("process 'workspace/configuration' for target-sql-backend request with scope URI") {
      var isLogMessageTriggeredCorrectly = false

      val projectMock = mockk<Project>()
      val configurationItemMock = mockk<ConfigurationItem> {
        every { scopeUri } returns "test"
        every { section } returns SETTINGS_SQL_BACKEND
      }
      val configurationParamsMock = mockk<ConfigurationParams> {
        every { items } returns listOf(configurationItemMock)
      }
      val cobolLanguageClient = spyk(CobolLanguageClient(projectMock))
      every { cobolLanguageClient.logMessage(any<MessageParams>()) } answers {
        if (firstArg<MessageParams>().message.contains(SETTINGS_SQL_BACKEND)) {
          isLogMessageTriggeredCorrectly = true
        }
      }

      val result = cobolLanguageClient.configuration(configurationParamsMock).join()

      assertSoftly { isLogMessageTriggeredCorrectly shouldBe true }
      assertSoftly { result shouldBeEqual listOf("DB2_SERVER") }
    }

    test("process 'workspace/configuration' for cpy-manager copybook-file-encoding request with scope URI") {
      var isLogMessageTriggeredCorrectly = false

      val projectMock = mockk<Project>()
      val configurationItemMock = mockk<ConfigurationItem> {
        every { scopeUri } returns "test"
        every { section } returns SETTINGS_CPY_FILE_ENCODING
      }
      val configurationParamsMock = mockk<ConfigurationParams> {
        every { items } returns listOf(configurationItemMock)
      }
      val cobolLanguageClient = spyk(CobolLanguageClient(projectMock))
      every { cobolLanguageClient.logMessage(any<MessageParams>()) } answers {
        if (firstArg<MessageParams>().message.contains(SETTINGS_CPY_FILE_ENCODING)) {
          isLogMessageTriggeredCorrectly = true
        }
      }

      val result = cobolLanguageClient.configuration(configurationParamsMock).join()

      assertSoftly { isLogMessageTriggeredCorrectly shouldBe true }
      assertSoftly { result shouldBeEqual listOf() }
    }

    test("process 'workspace/configuration' for cobol-lsp compiler options request with scope URI") {
      var isLogMessageTriggeredCorrectly = false

      val projectMock = mockk<Project>()
      val configurationItemMock = mockk<ConfigurationItem> {
        every { scopeUri } returns "test"
        every { section } returns SETTINGS_COMPILE_OPTIONS
      }
      val configurationParamsMock = mockk<ConfigurationParams> {
        every { items } returns listOf(configurationItemMock)
      }
      val cobolLanguageClient = spyk(CobolLanguageClient(projectMock))
      every { cobolLanguageClient.logMessage(any<MessageParams>()) } answers {
        if (firstArg<MessageParams>().message.contains(SETTINGS_COMPILE_OPTIONS)) {
          isLogMessageTriggeredCorrectly = true
        }
      }

      val result = cobolLanguageClient.configuration(configurationParamsMock).join()

      assertSoftly { isLogMessageTriggeredCorrectly shouldBe true }
      assertSoftly { result shouldBeEqual listOf(null) }
    }

    test("process 'workspace/configuration' for cobol-lsp logging level root request with scope URI") {
      var isLogMessageTriggeredCorrectly = false

      val projectMock = mockk<Project>()
      val configurationItemMock = mockk<ConfigurationItem> {
        every { scopeUri } returns "test"
        every { section } returns SETTINGS_CLIENT_LOGGING_LEVEL
      }
      val configurationParamsMock = mockk<ConfigurationParams> {
        every { items } returns listOf(configurationItemMock)
      }
      val cobolLanguageClient = spyk(CobolLanguageClient(projectMock))
      every { cobolLanguageClient.logMessage(any<MessageParams>()) } answers {
        if (firstArg<MessageParams>().message.contains(SETTINGS_CLIENT_LOGGING_LEVEL)) {
          isLogMessageTriggeredCorrectly = true
        }
      }

      val result = cobolLanguageClient.configuration(configurationParamsMock).join()

      assertSoftly { isLogMessageTriggeredCorrectly shouldBe true }
      assertSoftly { result shouldBeEqual listOf("ERROR") }
    }

    test("process 'workspace/configuration' for cobol-lsp locale request with scope URI") {
      var isLogMessageTriggeredCorrectly = false

      val projectMock = mockk<Project>()
      val configurationItemMock = mockk<ConfigurationItem> {
        every { scopeUri } returns "test"
        every { section } returns SETTINGS_LOCALE
      }
      val configurationParamsMock = mockk<ConfigurationParams> {
        every { items } returns listOf(configurationItemMock)
      }
      val cobolLanguageClient = spyk(CobolLanguageClient(projectMock))
      every { cobolLanguageClient.logMessage(any<MessageParams>()) } answers {
        if (firstArg<MessageParams>().message.contains(SETTINGS_LOCALE)) {
          isLogMessageTriggeredCorrectly = true
        }
      }

      val result = cobolLanguageClient.configuration(configurationParamsMock).join()

      assertSoftly { isLogMessageTriggeredCorrectly shouldBe true }
      assertSoftly { result shouldBeEqual listOf("en") }
    }

    test("process 'workspace/configuration' for cobol-lsp cobol program layout request with scope URI") {
      var isLogMessageTriggeredCorrectly = false

      val projectMock = mockk<Project>()
      val configurationItemMock = mockk<ConfigurationItem> {
        every { scopeUri } returns "test"
        every { section } returns SETTINGS_COBOL_PROGRAM_LAYOUT
      }
      val configurationParamsMock = mockk<ConfigurationParams> {
        every { items } returns listOf(configurationItemMock)
      }
      val cobolLanguageClient = spyk(CobolLanguageClient(projectMock))
      every { cobolLanguageClient.logMessage(any<MessageParams>()) } answers {
        if (firstArg<MessageParams>().message.contains(SETTINGS_COBOL_PROGRAM_LAYOUT)) {
          isLogMessageTriggeredCorrectly = true
        }
      }

      val result = cobolLanguageClient.configuration(configurationParamsMock).join()

      assertSoftly { isLogMessageTriggeredCorrectly shouldBe true }
      assertSoftly { result shouldBeEqual listOf(null) }
    }

    test("process 'workspace/configuration' for cobol-lsp subroutine-manager paths-local request with scope URI") {
      var isLogMessageTriggeredCorrectly = false

      val projectMock = mockk<Project>()
      val configurationItemMock = mockk<ConfigurationItem> {
        every { scopeUri } returns "test"
        every { section } returns SETTINGS_SUBROUTINE_LOCAL_PATH
      }
      val configurationParamsMock = mockk<ConfigurationParams> {
        every { items } returns listOf(configurationItemMock)
      }
      val cobolLanguageClient = spyk(CobolLanguageClient(projectMock))
      every { cobolLanguageClient.logMessage(any<MessageParams>()) } answers {
        if (firstArg<MessageParams>().message.contains(SETTINGS_SUBROUTINE_LOCAL_PATH)) {
          isLogMessageTriggeredCorrectly = true
        }
      }

      val result = cobolLanguageClient.configuration(configurationParamsMock).join()

      assertSoftly { isLogMessageTriggeredCorrectly shouldBe true }
      assertSoftly { result shouldBeEqual listOf(emptyList<String>()) }
    }

    test("process 'workspace/configuration' for cobol-lsp cics translator request with scope URI") {
      var isLogMessageTriggeredCorrectly = false

      val projectMock = mockk<Project>()
      val configurationItemMock = mockk<ConfigurationItem> {
        every { scopeUri } returns "test"
        every { section } returns SETTINGS_CICS_TRANSLATOR
      }
      val configurationParamsMock = mockk<ConfigurationParams> {
        every { items } returns listOf(configurationItemMock)
      }
      val cobolLanguageClient = spyk(CobolLanguageClient(projectMock))
      every { cobolLanguageClient.logMessage(any<MessageParams>()) } answers {
        if (firstArg<MessageParams>().message.contains(SETTINGS_CICS_TRANSLATOR)) {
          isLogMessageTriggeredCorrectly = true
        }
      }

      val result = cobolLanguageClient.configuration(configurationParamsMock).join()

      assertSoftly { isLogMessageTriggeredCorrectly shouldBe true }
      assertSoftly { result shouldBeEqual listOf("true") }
    }
    test("process 'workspace/configuration' for cobol-lsp unrecognized request with scope URI") {
      var isLogMessageTriggeredCorrectly = false

      val someTestSection = "test-unrecognized-section"
      val projectMock = mockk<Project>()
      val configurationItemMock = mockk<ConfigurationItem> {
        every { scopeUri } returns "test"
        every { section } returns someTestSection
      }
      val configurationParamsMock = mockk<ConfigurationParams> {
        every { items } returns listOf(configurationItemMock)
      }
      val cobolLanguageClient = spyk(CobolLanguageClient(projectMock))
      every { cobolLanguageClient.logMessage(any<MessageParams>()) } answers {
        if (firstArg<MessageParams>().message.contains(someTestSection)) {
          isLogMessageTriggeredCorrectly = true
        }
      }

      val result = cobolLanguageClient.configuration(configurationParamsMock).join()

      assertSoftly { isLogMessageTriggeredCorrectly shouldBe true }
      assertSoftly { result shouldBeEqual listOf() }
    }
  }

})
