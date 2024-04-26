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

import java.io.BufferedInputStream
import java.io.InputStream

/**
 * Input stream wrapper for COBOL LSP client. Is used for debug purposes
 * @property inputStream the input stream to wrap
 */
class CobolInputStreamWrapper(inputStream: InputStream) : BufferedInputStream(inputStream) {
  /**
   * Read the next message in the inputStream to provide debug information.
   * It will result in debug message to the [System.out] when there is a new content received by the client.
   * The original processing is continued by the [BufferedInputStream]'s 'read' method
   */
  override fun read(b: ByteArray, off: Int, len: Int): Int {
    val read = super.read(b, off, len)
    println("LSP Client receives: ${String(b, Charsets.UTF_8)}")
    return read
  }
}