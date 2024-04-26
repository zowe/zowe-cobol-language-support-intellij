/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright IBA Group 2024
 */

package org.zowe.cobol.init

import com.intellij.ide.plugins.DynamicPluginListener
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.openapi.application.runWriteAction

/** COBOL Language Support plug-in listener for functions before loading/unloading handling */
@OptIn(InitializationOnly::class)
class CobolPluginListener : DynamicPluginListener {
  /** Disable all the plug-in's related features before it is unloaded */
  override fun beforePluginUnload(pluginDescriptor: IdeaPluginDescriptor, isUpdate: Boolean) {
    val cobolPluginStates = CobolPluginState.getAllPluginStates()
    runWriteAction {
      cobolPluginStates.forEach { (_, pluginState) ->
        pluginState.disableTextMateBundle()
        pluginState.disableLSP()
        pluginState.finishDeinitialization()
      }
    }
    super.beforePluginUnload(pluginDescriptor, isUpdate)
  }
}