/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project
 */

package org.zowe.cobol.lsp

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.LanguageServerFactory
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider
import kotlinx.coroutines.runBlocking
import org.zowe.cobol.init.CobolPluginState
import org.zowe.cobol.init.InitializationOnly

// TODO: doc
@OptIn(InitializationOnly::class)
class CobolLanguageServerFactory : LanguageServerFactory {

  override fun createConnectionProvider(project: Project): StreamConnectionProvider {
    val pliPluginState = CobolPluginState.getPluginState(project)
    runBlocking {
      pliPluginState.unpackVSIX()
    }
    return pliPluginState.loadLanguageServerDefinition(project)
  }

  override fun createLanguageClient(project: Project): LanguageClientImpl {
    val pliPluginState = CobolPluginState.getPluginState(project)
    return pliPluginState.loadLanguageClientDefinition(project)
  }

}