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

import com.intellij.icons.AllIcons
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.fileTypes.ex.FileTypeIdentifiableByVirtualFile
import com.intellij.openapi.vfs.VirtualFile

/** Registered COBOL file type. Provides necessary info about the file type to apply language features */
class CobolFileType : LanguageFileType(CobolLanguage.INSTANCE), FileTypeIdentifiableByVirtualFile {
  @Suppress("CompanionObjectInExtension")
  companion object {
    @JvmField
    val INSTANCE = CobolFileType()
  }

  override fun getName() = "cobol-zowe"
  override fun getDescription() = "COBOL file highlighted by Zowe"
  override fun getDefaultExtension() = ""
  override fun getIcon() = AllIcons.FileTypes.Text
  override fun isMyFileType(file: VirtualFile): Boolean {
    return CobolFileTypeDefinerService.getService().isCobolFile(file)
  }
}
