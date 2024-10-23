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

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.io.FileReader
import java.io.Reader
import java.nio.file.Paths
import kotlin.reflect.KFunction

class VSCodeSettingsAdapterServiceTestSpec : FunSpec({
  context("VSCodeSettingsAdapterService.getListOfStringsConfiguration") {
    afterTest {
      unmockkAll()
      clearAllMocks()
    }

    test("get list of strings as the result of a request to the adapter service") {
      val testWorkspacePath = Paths.get("test_workspace")

      val settingsJsonReaderMock = mockk<FileReader>()

      val jsonArray = JsonArray()
      jsonArray.add(mockk<JsonElement>{ every { asString } returns "test_elem" })

      val jsonObj = mockk<JsonObject>()
      every { jsonObj.get(Sections.UNRECOGNIZED.toString()) } returns mockk {
        every { asJsonArray } returns jsonArray
      }

      val parseReaderFun: (Reader) -> JsonElement = JsonParser::parseReader
      mockkStatic(parseReaderFun as KFunction<*>)
      every { parseReaderFun(settingsJsonReaderMock) } returns mockk {
        every { isJsonObject } returns true
        every { asJsonObject } returns jsonObj
      }

      mockkObject(VSCodeSettingsAdapterService, recordPrivateCalls = true)
      val vscodeAdapterService = spyk<VSCodeSettingsAdapterService>()
      every {
        vscodeAdapterService invoke "readConfigFileSection" withArguments listOf(testWorkspacePath, any<(FileReader) -> Any?>())
      } answers {
        secondArg<(FileReader) -> List<String>>().invoke(settingsJsonReaderMock)
      }
      every { VSCodeSettingsAdapterService.getService() } returns vscodeAdapterService
      val result = vscodeAdapterService.getListOfStringsConfiguration(testWorkspacePath, Sections.UNRECOGNIZED)
      assertSoftly { result shouldBe listOf("test_elem") }
    }
    test("get empty list cause there is no related section in the json file") {
      val testWorkspacePath = Paths.get("test_workspace")

      val settingsJsonReaderMock = mockk<FileReader>()

      val jsonArray = JsonArray()
      jsonArray.add(mockk<JsonElement>{ every { asString } returns "test_elem" })

      val jsonObj = mockk<JsonObject>()
      every { jsonObj.get(Sections.UNRECOGNIZED.toString()) } returns mockk {
        every { asJsonArray } returns null
      }

      val parseReaderFun: (Reader) -> JsonElement = JsonParser::parseReader
      mockkStatic(parseReaderFun as KFunction<*>)
      every { parseReaderFun(settingsJsonReaderMock) } returns mockk {
        every { isJsonObject } returns true
        every { asJsonObject } returns jsonObj
      }

      mockkObject(VSCodeSettingsAdapterService, recordPrivateCalls = true)
      val vscodeAdapterService = spyk<VSCodeSettingsAdapterService>()
      every {
        vscodeAdapterService invoke "readConfigFileSection" withArguments listOf(testWorkspacePath, any<(FileReader) -> Any?>())
      } answers {
        secondArg<(FileReader) -> List<String>>().invoke(settingsJsonReaderMock)
      }
      every { VSCodeSettingsAdapterService.getService() } returns vscodeAdapterService
      val result = vscodeAdapterService.getListOfStringsConfiguration(testWorkspacePath, Sections.UNRECOGNIZED)
      assertSoftly { result shouldBe listOf() }
    }
    test("get empty list cause the json file is not a json object") {
      val testWorkspacePath = Paths.get("test_workspace")

      val settingsJsonReaderMock = mockk<FileReader>()

      val parseReaderFun: (Reader) -> JsonElement = JsonParser::parseReader
      mockkStatic(parseReaderFun as KFunction<*>)
      every { parseReaderFun(settingsJsonReaderMock) } returns mockk {
        every { isJsonObject } returns false
      }

      mockkObject(VSCodeSettingsAdapterService, recordPrivateCalls = true)
      val vscodeAdapterService = spyk<VSCodeSettingsAdapterService>()
      every {
        vscodeAdapterService invoke "readConfigFileSection" withArguments listOf(testWorkspacePath, any<(FileReader) -> Any?>())
      } answers {
        secondArg<(FileReader) -> List<String>>().invoke(settingsJsonReaderMock)
      }
      every { VSCodeSettingsAdapterService.getService() } returns vscodeAdapterService
      val result = vscodeAdapterService.getListOfStringsConfiguration(testWorkspacePath, Sections.UNRECOGNIZED)
      assertSoftly { result shouldBe listOf() }
    }
    test("get empty list cause the json file is not found") {
      val testWorkspacePath = Paths.get("test_workspace")

      mockkObject(VSCodeSettingsAdapterService, recordPrivateCalls = true)
      val vscodeAdapterService = spyk<VSCodeSettingsAdapterService>()
      every {
        vscodeAdapterService invoke "readConfigFileSection" withArguments listOf(testWorkspacePath, any<(FileReader) -> Any?>())
      } returns null
      every { VSCodeSettingsAdapterService.getService() } returns vscodeAdapterService
      val result = vscodeAdapterService.getListOfStringsConfiguration(testWorkspacePath, Sections.UNRECOGNIZED)
      assertSoftly { result shouldBe listOf() }
    }
  }
})
