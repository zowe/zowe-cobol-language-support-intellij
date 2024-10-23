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

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.zowe.cobol.searchForFileInPath
import java.nio.file.Paths

class CobolCopybooksServiceTestSpec : FunSpec({
  context("CobolCopybooksService.resolveCopybookPath") {
    afterTest {
      unmockkAll()
      clearAllMocks()
    }

    test("resolve copybook path") {
      val testWorkspacePathStr = "test_workspace"
      val testWorkspacePath = Paths.get(testWorkspacePathStr)
      val testCopybookName = "TSTCPY"
      val testCopybookFolders = listOf("test_res")
      val testExtensions = listOf(".TST")

      val fullTestPath = "$testWorkspacePathStr/${testCopybookFolders[0]}/$testCopybookName${testExtensions[0]}"

      mockkStatic(::searchForFileInPath)
      every { searchForFileInPath(any(), any(), any(), any()) } returns fullTestPath

      val cobolCopybooksService = spyk<CobolCopybooksService>()

      mockkObject(CobolCopybooksService)
      every { CobolCopybooksService.getService() } returns cobolCopybooksService

      val result = cobolCopybooksService
        .resolveCopybookPath(testWorkspacePath, testCopybookFolders, testCopybookName, testExtensions)
      val expected = Paths.get(fullTestPath).toUri().toString()
      assertSoftly { result shouldBe expected }
    }
    test("return null as no suitable copybook path found") {
      val testWorkspacePathStr = "test_workspace"
      val testWorkspacePath = Paths.get(testWorkspacePathStr)
      val testCopybookName = "TSTCPY"
      val testCopybookFolders = listOf("test_res")
      val testExtensions = listOf(".TST")

      mockkStatic(::searchForFileInPath)
      every { searchForFileInPath(any(), any(), any(), any()) } returns null

      val cobolCopybooksService = spyk<CobolCopybooksService>()

      mockkObject(CobolCopybooksService)
      every { CobolCopybooksService.getService() } returns cobolCopybooksService

      val result = cobolCopybooksService
        .resolveCopybookPath(testWorkspacePath, testCopybookFolders, testCopybookName, testExtensions)
      assertSoftly { result shouldBe null }
    }
    test("return null as there are no extensions") {
      val testWorkspacePathStr = "test_workspace"
      val testWorkspacePath = Paths.get(testWorkspacePathStr)
      val testCopybookName = "test"
      val testCopybookFolders = listOf<String>()
      val testExtensions = listOf<String>()

      val cobolCopybooksService = spyk<CobolCopybooksService>()

      mockkObject(CobolCopybooksService)
      every { CobolCopybooksService.getService() } returns cobolCopybooksService

      val result = cobolCopybooksService
        .resolveCopybookPath(testWorkspacePath, testCopybookFolders, testCopybookName, testExtensions)
      assertSoftly { result shouldBe null }
    }
    test("return null as there is no copybook name") {
      val testWorkspacePathStr = "test_workspace"
      val testWorkspacePath = Paths.get(testWorkspacePathStr)
      val testCopybookName = ""
      val testCopybookFolders = listOf<String>()
      val testExtensions = listOf<String>()

      val cobolCopybooksService = spyk<CobolCopybooksService>()

      mockkObject(CobolCopybooksService)
      every { CobolCopybooksService.getService() } returns cobolCopybooksService

      val result = cobolCopybooksService
        .resolveCopybookPath(testWorkspacePath, testCopybookFolders, testCopybookName, testExtensions)
      assertSoftly { result shouldBe null }
    }
  }
})
