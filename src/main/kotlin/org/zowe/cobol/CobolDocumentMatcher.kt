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

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.redhat.devtools.lsp4ij.AbstractDocumentMatcher

/** COBOL document matcher to recognize the COBOL file for the LSP to apply the server's features */
class CobolDocumentMatcher : AbstractDocumentMatcher() {
  override fun match(virtualFile: VirtualFile, project: Project): Boolean {
    return CobolFileTypeDefinerService.getService().isCobolFile(virtualFile)
  }
}
