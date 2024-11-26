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
import org.eclipse.lsp4j.WorkspaceFolder
import org.zowe.cobol.Sections
import org.zowe.cobol.VSCodeSettingsAdapterService
import java.net.URI
import java.util.concurrent.CompletableFuture
import kotlin.io.path.toPath

class CobolLanguageClientConfigurationTestSpec : FunSpec({

  context("CobolLanguageClientTestSpec.configuration") {
    afterTest {
      unmockkAll()
      clearAllMocks()
    }

    test("process 'workspace/configuration' for dialect registry section request") {
      var isLogMessageTriggeredCorrectly = false

      val projectMock = mockk<Project>()
      val configurationItemMock = mockk<ConfigurationItem> {
        every { section } returns Sections.DIALECT_REGISTRY.toString()
      }
      val configurationParamsMock = mockk<ConfigurationParams> {
        every { items } returns listOf(configurationItemMock)
      }
      val cobolLanguageClient = spyk(CobolLanguageClient(projectMock))
      every { cobolLanguageClient.logMessage(any<MessageParams>()) } answers {
        if (firstArg<MessageParams>().message.contains(Sections.DIALECT_REGISTRY.toString())) {
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
      every {
        cobolLanguageClient.workspaceFolders()
      } returns CompletableFuture.completedFuture(listOf(WorkspaceFolder("file:///c/test")))
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
        every { section } returns Sections.DIALECTS_SECTION.toString()
      }
      val configurationParamsMock = mockk<ConfigurationParams> {
        every { items } returns listOf(configurationItemMock)
      }
      val cobolLanguageClient = spyk(CobolLanguageClient(projectMock))
      every {
        cobolLanguageClient.workspaceFolders()
      } returns CompletableFuture.completedFuture(listOf(WorkspaceFolder("file:///c/test")))
      every { cobolLanguageClient.logMessage(any<MessageParams>()) } answers {
        if (firstArg<MessageParams>().message.contains(Sections.DIALECTS_SECTION.toString())) {
          isLogMessageTriggeredCorrectly = true
        }
      }

      val result = cobolLanguageClient.configuration(configurationParamsMock).join()

      assertSoftly { isLogMessageTriggeredCorrectly shouldBe true }
      assertSoftly { result shouldBeEqual listOf(emptyList<String>()) }
    }

    test("process 'workspace/configuration' for cpy-manager paths-local request with scope URI") {
      var isLogMessageTriggeredCorrectly = false

      val fakeUriPath = "file:///c/test"
      val fakeFinalPath = "final_test_path"

      val vscodeSettingsAdapterService = mockk<VSCodeSettingsAdapterService> {
        every {
          getListOfStringsConfiguration(URI.create(fakeUriPath).toPath(), Sections.CPY_LOCAL_PATH)
        } returns listOf()
      }
      mockkObject(VSCodeSettingsAdapterService)
      every { VSCodeSettingsAdapterService.getService() } returns vscodeSettingsAdapterService

      val cobolConfigsRecognitionService = mockk<CobolConfigsRecognitionService> {
        every {
          loadProcessorGroupCopybookPathsConfig(URI.create(fakeUriPath).toPath(), any<ConfigurationItem>(), listOf())
        } returns listOf(fakeFinalPath)
      }

      mockkObject(CobolConfigsRecognitionService)
      every { CobolConfigsRecognitionService.getService() } returns cobolConfigsRecognitionService

      val projectMock = mockk<Project>()
      val configurationItemMock = mockk<ConfigurationItem> {
        every { scopeUri } returns "test"
        every { section } returns Sections.CPY_LOCAL_PATH.toString()
      }
      val configurationParamsMock = mockk<ConfigurationParams> {
        every { items } returns listOf(configurationItemMock)
      }
      val cobolLanguageClient = spyk(CobolLanguageClient(projectMock))
      every {
        cobolLanguageClient.workspaceFolders()
      } returns CompletableFuture.completedFuture(listOf(WorkspaceFolder(fakeUriPath)))
      every { cobolLanguageClient.logMessage(any<MessageParams>()) } answers {
        if (firstArg<MessageParams>().message.contains(fakeFinalPath)) {
          isLogMessageTriggeredCorrectly = true
        }
      }

      val result = cobolLanguageClient.configuration(configurationParamsMock).join()

      assertSoftly { isLogMessageTriggeredCorrectly shouldBe true }
      assertSoftly { result shouldBeEqual listOf(listOf(fakeFinalPath)) }
    }

    test("process 'workspace/configuration' for dialect libs request with scope URI") {
      var isLogMessageTriggeredCorrectly = false

      val projectMock = mockk<Project>()
      val configurationItemMock = mockk<ConfigurationItem> {
        every { scopeUri } returns "test"
        every { section } returns Sections.DIALECT_LIBS.toString()
      }
      val configurationParamsMock = mockk<ConfigurationParams> {
        every { items } returns listOf(configurationItemMock)
      }
      val cobolLanguageClient = spyk(CobolLanguageClient(projectMock))
      every {
        cobolLanguageClient.workspaceFolders()
      } returns CompletableFuture.completedFuture(listOf(WorkspaceFolder("file:///c/test")))
      every { cobolLanguageClient.logMessage(any<MessageParams>()) } answers {
        if (firstArg<MessageParams>().message.contains(Sections.DIALECT_LIBS.toString())) {
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
        every { section } returns Sections.CPY_EXTENSIONS.toString()
      }
      val configurationParamsMock = mockk<ConfigurationParams> {
        every { items } returns listOf(configurationItemMock)
      }
      val cobolLanguageClient = spyk(CobolLanguageClient(projectMock))
      every {
        cobolLanguageClient.workspaceFolders()
      } returns CompletableFuture.completedFuture(listOf(WorkspaceFolder("file:///c/test")))
      every { cobolLanguageClient.logMessage(any<MessageParams>()) } answers {
        if (firstArg<MessageParams>().message.contains(Sections.CPY_EXTENSIONS.toString())) {
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
        every { section } returns Sections.SQL_BACKEND.toString()
      }
      val configurationParamsMock = mockk<ConfigurationParams> {
        every { items } returns listOf(configurationItemMock)
      }
      val cobolLanguageClient = spyk(CobolLanguageClient(projectMock))
      every {
        cobolLanguageClient.workspaceFolders()
      } returns CompletableFuture.completedFuture(listOf(WorkspaceFolder("file:///c/test")))
      every { cobolLanguageClient.logMessage(any<MessageParams>()) } answers {
        if (firstArg<MessageParams>().message.contains(Sections.SQL_BACKEND.toString())) {
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
        every { section } returns Sections.CPY_FILE_ENCODING.toString()
      }
      val configurationParamsMock = mockk<ConfigurationParams> {
        every { items } returns listOf(configurationItemMock)
      }
      val cobolLanguageClient = spyk(CobolLanguageClient(projectMock))
      every {
        cobolLanguageClient.workspaceFolders()
      } returns CompletableFuture.completedFuture(listOf(WorkspaceFolder("file:///c/test")))
      every { cobolLanguageClient.logMessage(any<MessageParams>()) } answers {
        if (firstArg<MessageParams>().message.contains(Sections.CPY_FILE_ENCODING.toString())) {
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
        every { section } returns Sections.COMPILER_OPTIONS.toString()
      }
      val configurationParamsMock = mockk<ConfigurationParams> {
        every { items } returns listOf(configurationItemMock)
      }
      val cobolLanguageClient = spyk(CobolLanguageClient(projectMock))
      every {
        cobolLanguageClient.workspaceFolders()
      } returns CompletableFuture.completedFuture(listOf(WorkspaceFolder("file:///c/test")))
      every { cobolLanguageClient.logMessage(any<MessageParams>()) } answers {
        if (firstArg<MessageParams>().message.contains(Sections.COMPILER_OPTIONS.toString())) {
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
        every { section } returns Sections.LOGGIN_LEVEL_ROOT.toString()
      }
      val configurationParamsMock = mockk<ConfigurationParams> {
        every { items } returns listOf(configurationItemMock)
      }
      val cobolLanguageClient = spyk(CobolLanguageClient(projectMock))
      every {
        cobolLanguageClient.workspaceFolders()
      } returns CompletableFuture.completedFuture(listOf(WorkspaceFolder("file:///c/test")))
      every { cobolLanguageClient.logMessage(any<MessageParams>()) } answers {
        if (firstArg<MessageParams>().message.contains(Sections.LOGGIN_LEVEL_ROOT.toString())) {
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
        every { section } returns Sections.LOCALE.toString()
      }
      val configurationParamsMock = mockk<ConfigurationParams> {
        every { items } returns listOf(configurationItemMock)
      }
      val cobolLanguageClient = spyk(CobolLanguageClient(projectMock))
      every {
        cobolLanguageClient.workspaceFolders()
      } returns CompletableFuture.completedFuture(listOf(WorkspaceFolder("file:///c/test")))
      every { cobolLanguageClient.logMessage(any<MessageParams>()) } answers {
        if (firstArg<MessageParams>().message.contains(Sections.LOCALE.toString())) {
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
        every { section } returns Sections.COBOL_PROGRAM_LAYOUT.toString()
      }
      val configurationParamsMock = mockk<ConfigurationParams> {
        every { items } returns listOf(configurationItemMock)
      }
      val cobolLanguageClient = spyk(CobolLanguageClient(projectMock))
      every {
        cobolLanguageClient.workspaceFolders()
      } returns CompletableFuture.completedFuture(listOf(WorkspaceFolder("file:///c/test")))
      every { cobolLanguageClient.logMessage(any<MessageParams>()) } answers {
        if (firstArg<MessageParams>().message.contains(Sections.COBOL_PROGRAM_LAYOUT.toString())) {
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
        every { section } returns Sections.SUBROUTINE_LOCAL_PATH.toString()
      }
      val configurationParamsMock = mockk<ConfigurationParams> {
        every { items } returns listOf(configurationItemMock)
      }
      val cobolLanguageClient = spyk(CobolLanguageClient(projectMock))
      every {
        cobolLanguageClient.workspaceFolders()
      } returns CompletableFuture.completedFuture(listOf(WorkspaceFolder("file:///c/test")))
      every { cobolLanguageClient.logMessage(any<MessageParams>()) } answers {
        if (firstArg<MessageParams>().message.contains(Sections.SUBROUTINE_LOCAL_PATH.toString())) {
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
        every { section } returns Sections.CICS_TRANSLATOR.toString()
      }
      val configurationParamsMock = mockk<ConfigurationParams> {
        every { items } returns listOf(configurationItemMock)
      }
      val cobolLanguageClient = spyk(CobolLanguageClient(projectMock))
      every {
        cobolLanguageClient.workspaceFolders()
      } returns CompletableFuture.completedFuture(listOf(WorkspaceFolder("file:///c/test")))
      every { cobolLanguageClient.logMessage(any<MessageParams>()) } answers {
        if (firstArg<MessageParams>().message.contains(Sections.CICS_TRANSLATOR.toString())) {
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
      every {
        cobolLanguageClient.workspaceFolders()
      } returns CompletableFuture.completedFuture(listOf(WorkspaceFolder("file:///c/test")))
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
