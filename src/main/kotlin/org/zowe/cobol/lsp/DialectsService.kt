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

package org.zowe.cobol.lsp

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import java.net.URI

/** Service class that provides read/write dialect settings functionality */
@Service
class DialectsService {

  private val dialects: MutableMap<String, DialectInfo> = mutableMapOf()

  companion object {
    fun getService(): DialectsService = service()
  }

  /**
   * Gets registered [DialectInfo]s
   * @return the list of [DialectInfo]s
   */
  fun getDialects(): List<DialectInfo> {
    return dialects.values.toList()
  }

  /** Clears the registry */
  fun clear() {
    dialects.clear()
  }

  /**
   * Registers dialect in the system
   * @param extensionId the extension id
   * @param name the name of the dialect
   * @param uri the path to jar file
   * @param description the description of the dialect
   * @param snippetPath the snippet map path for the dialect
   */
  fun register(extensionId: String, name: String, uri: URI, description: String, snippetPath: String) {
    val dialectInfo = DialectInfo(name, uri, description, extensionId, snippetPath)
    dialects[dialectInfo.name] = dialectInfo
  }

  /**
   * Unregisters dialect from the system
   * @param name the name of the dialect
   */
  fun unregister(name: String) {
    dialects.remove(name)
  }

}