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

import java.io.BufferedOutputStream
import java.io.OutputStream

/**
 * Output stream wrapper for COBOL LSP client. Is used for debug purposes
 * @property outputStream the output stream to wrap
 */
class CobolOutputStreamWrapper(outputStream: OutputStream) : BufferedOutputStream(outputStream) {
  /**
   * Read the next message to the outputStream to provide debug information.
   * It will result in debug message to the [System.out] when there is a new content received by the client.
   * The original processing is continued by the [BufferedOutputStream]'s 'write' method
   */
  override fun write(b: ByteArray, off: Int, len: Int) {
    println("LSP Client sends: ${String(b, Charsets.UTF_8)}")
    super.write(b, off, len)
  }
}