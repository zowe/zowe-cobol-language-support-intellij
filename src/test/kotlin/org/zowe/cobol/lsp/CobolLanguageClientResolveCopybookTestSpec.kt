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

class CobolLanguageClientResolveCopybookTestSpec : FunSpec({

  context("CobolLanguageClientTestSpec.resolveCopybook") {
    afterTest {
      unmockkAll()
      clearAllMocks()
    }

    test("process 'copybook/resolve' request") {
      val fakeUriPath = "file:///c/test"
      val testCopybookName = "TSTCPY"
      val testCpyExtensions = listOf(".testext")
      val testCpyLocalPaths = listOf("test_local_path")
      val fakeFinalPath = "final_test_path"

      val cobolCopybooksService = mockk<CobolCopybooksService> {
        every {
          resolveCopybookPath(URI.create(fakeUriPath).toPath(), testCpyLocalPaths, testCopybookName, testCpyExtensions)
        } returns fakeFinalPath
      }
      mockkObject(CobolCopybooksService)
      every { CobolCopybooksService.getService() } returns cobolCopybooksService

      val projectMock = mockk<Project>()
      val cobolLanguageClient = spyk(CobolLanguageClient(projectMock))
      every {
        cobolLanguageClient.workspaceFolders()
      } returns CompletableFuture.completedFuture(listOf(WorkspaceFolder(fakeUriPath)))
      every {
        cobolLanguageClient.configuration(any<ConfigurationParams>())
      } returns CompletableFuture.completedFuture(mutableListOf(testCpyExtensions, testCpyLocalPaths))

      val result = cobolLanguageClient.resolveCopybook("test", testCopybookName, "test").join()
      assertSoftly { result shouldBe fakeFinalPath }
    }
  }

})
