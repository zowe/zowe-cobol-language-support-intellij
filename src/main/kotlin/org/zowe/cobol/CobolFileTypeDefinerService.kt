/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 *
 * Contributors:
 *   Zowe Community
 *   Uladzislau Kalesnikau
 */

package org.zowe.cobol

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.vfs.VirtualFile
import org.zowe.explorer.v3.lang.LanguageByContentRecognizerService

/** COBOL file type definer service to recognize COBOL language by the provided virtual file */
@Service
class CobolFileTypeDefinerService {
  companion object {
    @JvmStatic
    fun getService(): CobolFileTypeDefinerService = service()
  }

  private val langByVFileCache = mutableMapOf<VirtualFile, Pair<Int, String>>()

  /**
   * Check if the provided [virtualFile] a COBOL file.
   * Uses org.zowe.explorer.v3.lang.LanguageByContentRecognizerService Zowe Explorer service to recognize the content.
   * The recognition process is run only when the file is a Zowe Explorer dataset
   * @param virtualFile the virtual file to define the content by
   * @return true if the content of the file is defined as COBOL, false otherwise
   */
  fun isCobolFile(virtualFile: VirtualFile): Boolean {
    return try {
      Class.forName("org.zowe.explorer.v3.lang.LanguageByContentRecognizerService")
      if (virtualFile.path.contains("Zowe Explorer") && virtualFile.path.contains("Data Sets")) {
        if (!langByVFileCache.contains(virtualFile) || (langByVFileCache[virtualFile]?.second ?: "") == "") {
          val (recognitionTries, lang) = langByVFileCache[virtualFile] ?: (0 to "")
          val (newRecognitionTries, newLang) = if (lang == "" && recognitionTries < 10) {
            val newRecognizedLang = LanguageByContentRecognizerService.getService()
              .getFileContentLanguage(virtualFile)
            if (newRecognizedLang != lang) recognitionTries + 1 to newRecognizedLang else recognitionTries to lang
          } else recognitionTries to lang
          langByVFileCache[virtualFile] = newRecognitionTries to newLang
        }
        (langByVFileCache[virtualFile]?.second ?: "") == "cbl"
      } else false
    } catch (ignore: Exception) {
      false
    }
  }
}
