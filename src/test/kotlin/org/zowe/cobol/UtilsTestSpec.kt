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

package org.zowe.cobol

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.nio.file.*

class UtilsTestSpec : FunSpec({
  context("utils.kt tests") {
    afterTest {
      unmockkAll()
      clearAllMocks()
    }

    context("findRelatedFilesInPaths") {
      test("return a list of found files") {
        mockkStatic(FileSystems::getDefault)
        val testPatterns = listOf("test1", "test2", "test3")
        val testPath1 = Paths.get("test1")
        val testPaths = listOf(testPath1, Paths.get("test4")).stream()

        val mockedDefault = mockk<FileSystem> {
          every { getPathMatcher(any()) } answers {
            if (firstArg<String>() == "glob:**test1") {
              mockk<PathMatcher> {
                every { matches(any()) } answers {
                  firstArg<Path>() == testPath1
                }
              }
            } else {
              mockk<PathMatcher> {
                every { matches(any()) } returns false
              }
            }
          }
        }
        every { FileSystems.getDefault() } returns mockedDefault

        val result = findRelatedFilesInPaths(testPatterns, testPaths)
        assertSoftly { result shouldBe listOf(testPath1.toString()) }
      }
    }
    context("searchForFileInPath") {
      test("return found path in absolute resource path as folder, that contains the provided file path") {
        val testWorkspaceStr = "test_workspace"
        val testResourceStr = "test1"
        var isPathResolvedWithParent = false

        mockkStatic(::findFilesInPath)
        every { findFilesInPath(any(), any()) } answers { listOf(testResourceStr) }

        val workspacePathMock = mockk<Path>()
        every { workspacePathMock.resolve(any<String>()) } answers {
          isPathResolvedWithParent = true
          workspacePathMock
        }

        mockkStatic(FileSystems::getDefault)
        val defaultFileSystem = mockk<FileSystem> {
          every { getPath(any(), *anyVararg()) } answers {
            val pathStr = firstArg<String>()
            when (pathStr) {
              testWorkspaceStr -> workspacePathMock
              testResourceStr -> {
                mockk {
                  every { isAbsolute } returns true
                  every { resolve(any<String>()) } returns this
                }
              }
              else -> mockk()
            }
          }
        }
        every { FileSystems.getDefault() } returns defaultFileSystem

        val testWorkspace = Paths.get(testWorkspaceStr)

        val result = searchForFileInPath(
          testWorkspace,
          "$testResourceStr?with[magic]",
          "test_file",
          ".cpy"
        )
        assertSoftly { result shouldBe testResourceStr }
        assertSoftly { isPathResolvedWithParent shouldBe false }
      }
      test("return found path in absolute resource path as file, that corresponds to the provided file path") {
        val testWorkspaceStr = "test_workspace"
        val testResourceStr = "test1/test_file.cpy"
        var isPathResolvedWithParent = false
        var isFindFilesInPathCalled = false

        mockkStatic(::findFilesInPath)
        every {
          findFilesInPath(any(), any())
        } answers {
          isFindFilesInPathCalled = true
          listOf(testResourceStr)
        }

        val workspacePathMock = mockk<Path>()
        every { workspacePathMock.resolve(any<String>()) } answers {
          isPathResolvedWithParent = true
          workspacePathMock
        }

        mockkStatic(FileSystems::getDefault)
        val defaultFileSystem = mockk<FileSystem> {
          every { getPath(any(), *anyVararg()) } answers {
            val pathStr = firstArg<String>()
            when (pathStr) {
              testWorkspaceStr -> workspacePathMock
              testResourceStr -> {
                mockk pathMockk@ {
                  every { isAbsolute } returns true
                  every { resolve(any<String>()) } returns this
                  every { this@pathMockk.toString() } returns "test1/test_file.cpy"
                }
              }
              else -> mockk()
            }
          }
        }
        every { FileSystems.getDefault() } returns defaultFileSystem

        val testWorkspace = Paths.get(testWorkspaceStr)

        val result = searchForFileInPath(
          testWorkspace,
          testResourceStr,
          "test_file",
          ".cpy"
        )
        assertSoftly { result shouldBe testResourceStr }
        assertSoftly { isPathResolvedWithParent shouldBe false }
        assertSoftly { isFindFilesInPathCalled shouldBe false }
      }
      test("return found path in workspace, that contains the provided file path") {
        val testWorkspaceStr = "test_workspace"
        val testResourceStr = "test1"
        var isPathResolvedWithParent = false

        mockkStatic(::findFilesInPath)
        every { findFilesInPath(any(), any()) } answers { listOf(testWorkspaceStr) }

        val workspacePathMock = mockk<Path>()
        every { workspacePathMock.resolve(any<String>()) } answers {
          isPathResolvedWithParent = true
          workspacePathMock
        }

        mockkStatic(FileSystems::getDefault)
        val defaultFileSystem = mockk<FileSystem> {
          every { getPath(any(), *anyVararg()) } answers {
            val pathStr = firstArg<String>()
            when (pathStr) {
              testWorkspaceStr -> workspacePathMock
              testResourceStr -> {
                mockk {
                  every { isAbsolute } returns false
                  every { resolve(any<String>()) } returns this
                }
              }
              else -> mockk()
            }
          }
        }
        every { FileSystems.getDefault() } returns defaultFileSystem

        val testWorkspace = Paths.get(testWorkspaceStr)

        val result = searchForFileInPath(
          testWorkspace,
          "$testResourceStr?with[magic]",
          "test_file",
          ".cpy"
        )
        assertSoftly { result shouldBe testWorkspaceStr }
        assertSoftly { isPathResolvedWithParent shouldBe true }
      }
      test("return null as path is not found in a workspace") {
        val testWorkspaceStr = "test_workspace"
        val testResourceStr = "test1"
        var isPathResolvedWithParent = false

        mockkStatic(::findFilesInPath)
        every { findFilesInPath(any(), any()) } answers { listOf() }

        val workspacePathMock = mockk<Path>()
        every { workspacePathMock.resolve(any<String>()) } answers {
          isPathResolvedWithParent = true
          workspacePathMock
        }

        mockkStatic(FileSystems::getDefault)
        val defaultFileSystem = mockk<FileSystem> {
          every { getPath(any(), *anyVararg()) } answers {
            val pathStr = firstArg<String>()
            when (pathStr) {
              testWorkspaceStr -> workspacePathMock
              testResourceStr -> {
                mockk {
                  every { isAbsolute } returns false
                  every { resolve(any<String>()) } returns this
                }
              }
              else -> mockk()
            }
          }
        }
        every { FileSystems.getDefault() } returns defaultFileSystem

        val testWorkspace = Paths.get(testWorkspaceStr)

        val result = searchForFileInPath(testWorkspace, testResourceStr, "test_file", ".cpy")
        assertSoftly { result shouldBe null }
        assertSoftly { isPathResolvedWithParent shouldBe true }
      }
    }
  }
})
