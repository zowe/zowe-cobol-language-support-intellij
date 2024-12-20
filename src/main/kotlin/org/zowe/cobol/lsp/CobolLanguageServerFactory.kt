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

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.LanguageServerFactory
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider
import org.zowe.cobol.state.COBOL_PLUGIN_NOTIFICATION_ID
import org.zowe.cobol.state.CobolPluginState
import org.zowe.cobol.state.InitializationOnly
import org.zowe.cobol.state.LanguageSupportStateService

/** COBOL language server factory to provide all the necessary functionalities for COBOL language support in the IDE */
class CobolLanguageServerFactory : LanguageServerFactory {

  override fun createConnectionProvider(project: Project): StreamConnectionProvider {
    val lsStateService = LanguageSupportStateService.getService()
    val pluginState = lsStateService.getPluginState(project) { CobolPluginState(project) }

    @OptIn(InitializationOnly::class)
    if (!pluginState.isLSPServerConnectionReady()) {
      pluginState.prepareVSIX {}
      pluginState.prepareLSPServerConnection {}
    }

    return pluginState.getReadyLSPServerConnection() as StreamConnectionProvider
  }

  override fun createLanguageClient(project: Project): LanguageClientImpl {
    val lsStateService = LanguageSupportStateService.getService()
    val pluginState = lsStateService.getPluginState(project) { CobolPluginState(project) }

    @OptIn(InitializationOnly::class)
    if (!pluginState.isLSPClientReady()) {
      pluginState.prepareLSPClient {}
      pluginState.finishInitialization(COBOL_PLUGIN_NOTIFICATION_ID) {}
    }

    return pluginState.getReadyLSPClient() as LanguageClientImpl
  }

}
