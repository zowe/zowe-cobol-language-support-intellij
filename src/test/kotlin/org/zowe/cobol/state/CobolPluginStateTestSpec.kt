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

package org.zowe.cobol.state

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.server.JavaProcessCommandBuilder
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.*
import org.junit.jupiter.api.assertThrows
import org.zowe.cobol.getPrivateFieldValue
import org.zowe.cobol.setPrivateFieldValue
import java.nio.file.Path
import kotlin.io.path.pathString

@OptIn(InitializationOnly::class)
class CobolPluginStateTestSpec : FunSpec({

  context("CobolPluginStateTestSpec.prepareVSIX") {
    lateinit var projectMock: Project
    lateinit var cobolState: CobolPluginState

    var isFinalPrepFunctionCalled = false
    var isUnpackVSIXCalled = false

    beforeTest {
      projectMock = mockk<Project>()
      cobolState = spyk(CobolPluginState(projectMock), recordPrivateCalls = true)

      isFinalPrepFunctionCalled = false
      isUnpackVSIXCalled = false

      every {
        cobolState["unpackVSIX"]()
      } answers {
        isUnpackVSIXCalled = true
        Unit
      }
    }

    afterTest {
      unmockkAll()
      clearAllMocks()
    }

    test("the state should change itself respectively after the VSIX unpacking process is finished") {
      every { cobolState["computeVSIXPlacingPaths"]() } returns false

      cobolState.prepareVSIX { isFinalPrepFunctionCalled = true }

      val currState = getPrivateFieldValue(
        cobolState,
        LanguageSupportState::class.java,
        "currState"
      ) as InitStates
      assertSoftly { isFinalPrepFunctionCalled shouldBe true }
      assertSoftly { isUnpackVSIXCalled shouldBe true }
      assertSoftly { currState shouldBe InitStates.VSIX_PREPARED }
    }

    test("the state should change itself respectively without additional steps") {
      every { cobolState["computeVSIXPlacingPaths"]() } returns true

      cobolState.prepareVSIX { isFinalPrepFunctionCalled = true }

      val currState = getPrivateFieldValue(
        cobolState,
        LanguageSupportState::class.java,
        "currState"
      ) as InitStates
      assertSoftly { isFinalPrepFunctionCalled shouldBe true }
      assertSoftly { isUnpackVSIXCalled shouldBe false }
      assertSoftly { currState shouldBe InitStates.VSIX_PREPARED}
    }

    test("the state should throw error cause the state before VSIX unpack is not correct") {
      setPrivateFieldValue(
        cobolState,
        LanguageSupportState::class.java,
        "currState",
        InitStates.VSIX_PREPARED
      )
      val exception = assertThrows<IllegalStateException> { cobolState.prepareVSIX { isFinalPrepFunctionCalled = true } }
      assertSoftly { exception.message shouldContain "Invalid plug-in state" }
      assertSoftly { isUnpackVSIXCalled shouldBe false }
    }
  }

  context("CobolPluginStateTestSpec.prepareLSPServerConnection") {
    lateinit var projectMock: Project
    lateinit var cobolState: CobolPluginState

    var isFinalPrepFunctionCalled = false

    beforeTest {
      projectMock = mockk<Project>()
      cobolState = spyk(CobolPluginState(projectMock), recordPrivateCalls = true)

      isFinalPrepFunctionCalled = false
    }

    afterTest {
      unmockkAll()
      clearAllMocks()
    }

    test("the state should change itself respectively after the LSP server connection instance preparation is finished") {
      val commandsListMock = mockk<MutableList<String>>()
      every { commandsListMock.add(any<String>()) } returns true

      val javaProcessCommandBuilderMock = mockk<JavaProcessCommandBuilder>()
      every { javaProcessCommandBuilderMock.setJar(any<String>()) } returns javaProcessCommandBuilderMock
      every { javaProcessCommandBuilderMock.create() } returns commandsListMock

      every { cobolState["getJavaProcessCommandBuilder"]() } returns javaProcessCommandBuilderMock

      setPrivateFieldValue(
        cobolState,
        LanguageSupportState::class.java,
        "currState",
        InitStates.VSIX_PREPARED
      )

      val lspServerPathMock = mockk<Path>()
      every { lspServerPathMock.pathString } returns ""

      setPrivateFieldValue(
        cobolState,
        CobolPluginState::class.java,
        "lspServerPath",
        lspServerPathMock
      )

      cobolState.prepareLSPServerConnection { isFinalPrepFunctionCalled = true }
      val currState = getPrivateFieldValue(
        cobolState,
        LanguageSupportState::class.java,
        "currState"
      ) as InitStates
      assertSoftly { isFinalPrepFunctionCalled shouldBe true }
      assertSoftly { currState shouldBe InitStates.LSP_SERVER_CONNECTION_PREPARED }
    }

    test("the state should throw error cause the state before LSP server connection preparation is not correct") {
      val exception = assertThrows<IllegalStateException> { cobolState.prepareLSPServerConnection { isFinalPrepFunctionCalled = true } }
      assertSoftly { exception.message shouldContain "Invalid plug-in state" }
    }
  }

})
