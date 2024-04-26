/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright IBA Group 2024
 */

package org.zowe.cobol.lsp.debug

import org.wso2.lsp4intellij.client.connection.ProcessStreamConnectionProvider

/**
 * COBOL LSP stream connection provider wrapper. Could be used for low-level debug purposes to see how the LSP client
 * communicates with the LSP server
 */
class CobolProcessStreamConnectionProvider(commands: List<String>, workingDir: String)
  : ProcessStreamConnectionProvider(commands, workingDir) {
//  override fun getOutputStream(): OutputStream? {
//    val rawOutputStream = super.getOutputStream()
//    return if (rawOutputStream != null) CobolOutputStreamWrapper(rawOutputStream) else null
//  }

//  override fun getInputStream(): InputStream? {
//    val rawInputStream = super.getInputStream()
//    return if (rawInputStream != null) CobolInputStreamWrapper(rawInputStream) else null
//  }
}
