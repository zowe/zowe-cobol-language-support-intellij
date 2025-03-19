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

import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.editor.highlighter.EditorHighlighter
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.plugins.textmate.language.syntax.highlighting.TextMateEditorHighlighterProvider

/** Editor highlighter provider for the COBOL code to be highlighted using a respective TextMate's bundle */
class CobolEditorHighlighterProvider : TextMateEditorHighlighterProvider() {
  override fun getEditorHighlighter(
    project: Project?,
    fileType: FileType,
    virtualFile: VirtualFile?,
    colors: EditorColorsScheme
  ): EditorHighlighter {
    return super.getEditorHighlighter(
      project,
      fileType,
      if (virtualFile != null) makeNoExtFileAsCbl(virtualFile) else null,
      colors
    )
  }
}
