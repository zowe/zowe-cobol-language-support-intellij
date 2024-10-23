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

import com.google.gson.JsonParser
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.nio.file.Path

/** VS Code settings adapter service. Needed to recognize configs in .vscode folder */
@Service
class VSCodeSettingsAdapterService {

  companion object {
    fun getService(): VSCodeSettingsAdapterService = service()
  }

  /**
   * Read the .vscode/settings.json file section
   * @param workspaceFolder the workspace folder path to search for the file in
   * @param readBlock the function to handle the read section
   * @return the result of the read section handling or error if file is not found
   */
  private fun <T : Any> readConfigFileSection(workspaceFolder: Path, readBlock: (FileReader) -> T?): T? {
    return try {
      val settingsPath = workspaceFolder.resolve(".vscode").resolve("settings.json")
      val settingsFile = File(settingsPath.toUri())
      FileReader(settingsFile).use(readBlock)
    } catch (e: FileNotFoundException) {
      // TODO: logger
//        println("settings.json file not found")
      null
    }
  }

  /**
   * Read the .vscode/settings.json file section and expect to return a list of strings as a result
   * @param workspaceFolder the workspace folder path to search for the file in
   * @param section the section to read
   * @return the list of strings, read from the section, or empty list on failure or if there are no items
   */
  fun getListOfStringsConfiguration(workspaceFolder: Path, section: Sections): List<String> {
    return readConfigFileSection(workspaceFolder) { settingsJsonReader ->
      val settingsJsonElement = JsonParser.parseReader(settingsJsonReader)
      if (settingsJsonElement.isJsonObject) {
        val settingsJsonObject = settingsJsonElement.asJsonObject
        val settingsDialectJsonArray = settingsJsonObject.get(section.toString())?.asJsonArray
        settingsDialectJsonArray?.map { it.asString } ?: listOf()
      } else {
        listOf()
      }
    } ?: listOf()
  }

}
