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
import com.intellij.openapi.project.ProjectManager
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.zowe.cobol.setPrivateFieldValue
import org.zowe.cobol.state.InitializationOnly
import org.zowe.cobol.state.LanguageSupportState
import org.zowe.cobol.state.LanguageSupportStateService

class LanguageSupportStateServiceTestSpec : FunSpec({

  context("LanguageSupportStateServiceTestSpec.getPluginState") {
    afterTest {
      unmockkAll()
      clearAllMocks()
    }

    test("get already initialized plugin state") {
      val projectMock = mockk<Project>()
      val lsStateMock = mockk<LanguageSupportState>()
      val defaultLSStateMock = mockk<LanguageSupportState>()

      val lsStateService = spyk(LanguageSupportStateService(), recordPrivateCalls = true)
      mockkObject(LanguageSupportStateService)
      every { LanguageSupportStateService.instance } returns lsStateService

      setPrivateFieldValue(
        lsStateService,
        LanguageSupportStateService::class.java,
        "projectToPluginState",
        mutableMapOf(projectMock to lsStateMock)
      )

      val result = LanguageSupportStateService.instance.getPluginState(projectMock) { defaultLSStateMock }

      assertSoftly { result shouldBe lsStateMock }
    }

    test("get newly initialized plugin state") {
      val projectMock = mockk<Project>()
      val lsStateMock = mockk<LanguageSupportState>()
      val defaultLSStateMock = mockk<LanguageSupportState>()

      val lsStateService = spyk(LanguageSupportStateService(), recordPrivateCalls = true)
      mockkObject(LanguageSupportStateService)
      every { LanguageSupportStateService.instance } returns lsStateService

      setPrivateFieldValue(
        lsStateService,
        LanguageSupportStateService::class.java,
        "projectToPluginState",
        mutableMapOf(mockk<Project>() to lsStateMock)
      )

      val result = LanguageSupportStateService.instance.getPluginState(projectMock) { defaultLSStateMock }

      assertSoftly { result shouldBe defaultLSStateMock }
    }
  }

})
