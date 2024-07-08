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
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl
import org.eclipse.lsp4j.ConfigurationParams
import org.eclipse.lsp4j.MessageParams
import org.eclipse.lsp4j.MessageType
import java.util.concurrent.CompletableFuture

const val DIALECT_REGISTRY_SECTION = "cobol-lsp.dialect.registry"
const val SETTINGS_DIALECT = "cobol-lsp.dialects"
const val SETTINGS_CPY_LOCAL_PATH = "cobol-lsp.cpy-manager.paths-local"
const val DIALECT_LIBS = "cobol-lsp.dialect.libs"
const val SETTINGS_CPY_EXTENSIONS = "cobol-lsp.cpy-manager.copybook-extensions"
const val SETTINGS_SQL_BACKEND = "cobol-lsp.target-sql-backend"
const val SETTINGS_CPY_FILE_ENCODING = "cobol-lsp.cpy-manager.copybook-file-encoding"
const val SETTINGS_COMPILE_OPTIONS = "cobol-lsp.compiler.options"
const val SETTINGS_CLIENT_LOGGING_LEVEL = "cobol-lsp.logging.level.root"
const val SETTINGS_LOCALE = "cobol-lsp.locale"
const val SETTINGS_COBOL_PROGRAM_LAYOUT = "cobol-lsp.cobol.program.layout"
const val SETTINGS_SUBROUTINE_LOCAL_PATH = "cobol-lsp.subroutine-manager.paths-local"
const val SETTINGS_CICS_TRANSLATOR = "cobol-lsp.cics.translator"

/** COBOL LSP client wrapper. Provides a comprehensive support for the COBOL LSP communications */
class CobolLanguageClient(project: Project) : LanguageClientImpl(project) {

  /**
   * "workspace/configuration" request from LSP server handler.
   * Computes the configuration values, requested by the server
   * @param configurationParams the configuration parameters, requested by the server
   * @return the resulting array of the parameters, corresponding to their order, wrapped in [CompletableFuture]
   */
  // TODO: clarify the configuration handler + write configuration wrappers for other sections
  override fun configuration(configurationParams: ConfigurationParams?): CompletableFuture<MutableList<Any?>> {
    val result = mutableListOf<Any?>()
    for (item in configurationParams?.items ?: emptyList()) {
      try {
        if (item.section == DIALECT_REGISTRY_SECTION) {
          logMessage(MessageParams(MessageType.Info, "${item.section} is not correctly recognized yet 1"))
          result.add(emptyList<String>())
//          val computed = DialectRegistry.getDialects()
//          result.add(computed)
        } else if (item.scopeUri != "") {
//          val cfg = vscode.workspace.getConfiguration().get(item.section)
          when (item.section) {
            SETTINGS_DIALECT -> {
              logMessage(MessageParams(MessageType.Info, "${item.section} is not correctly recognized yet 2"))
              result.add(emptyList<String>())
        //            val computed = loadProcessorGroupDialectConfig(item, cfg)
        //            result.add(computed)
            }
            SETTINGS_CPY_LOCAL_PATH -> {
              logMessage(MessageParams(MessageType.Info, "${item.section} is not recognized yet 3"))
        //            val computed = loadProcessorGroupCopybookPathsConfig(item, cfg as List<String>)
        //            result.add(computed)
        //          } else if (item.section === DIALECT_LIBS && !!item.dialect) {
            }
            DIALECT_LIBS -> {
              logMessage(MessageParams(MessageType.Info, "${item.section} is not recognized yet 4"))
        //            val dialectLibs = SettingsService.getCopybookLocalPath(item.scopeUri, item.dialect)
        //            result.add(dialectLibs)
            }
            SETTINGS_CPY_EXTENSIONS -> {
              logMessage(MessageParams(MessageType.Info, "${item.section} is not correctly recognized 5"))
              result.add(listOf(".CPY", ".COPY", ".cpy", ".copy",""))
        //            val computed = loadProcessorGroupCopybookExtensionsConfig(item, cfg as List<String>)
        //            result.add(computed)
            }
            SETTINGS_SQL_BACKEND -> {
              logMessage(MessageParams(MessageType.Info, "${item.section} is not correctly recognized yet 6"))
              result.add("DB2_SERVER")
        //            val computed = loadProcessorGroupSqlBackendConfig(item, cfg as String)
        //            result.add(computed)
            }
            SETTINGS_CPY_FILE_ENCODING -> {
              logMessage(MessageParams(MessageType.Info, "${item.section} is not recognized yet 7"))
        //            val computed = loadProcessorGroupCopybookEncodingConfig(item, cfg as String)
        //            result.add(computed)
            }
            SETTINGS_COMPILE_OPTIONS -> {
              logMessage(MessageParams(MessageType.Info, "${item.section} is not correctly recognized yet 8"))
              result.add(null)
        //            val computed = loadProcessorGroupCompileOptionsConfig(item, cfg as String)
        //            result.add(computed)
            }
            SETTINGS_CLIENT_LOGGING_LEVEL -> {
              logMessage(MessageParams(MessageType.Info, "${item.section} is not correctly recognized 11"))
              result.add("ERROR")
            }
            SETTINGS_LOCALE -> {
              logMessage(MessageParams(MessageType.Info, "${item.section} is not correctly recognized 12"))
              result.add("en")
            }
            SETTINGS_COBOL_PROGRAM_LAYOUT -> {
              logMessage(MessageParams(MessageType.Info, "${item.section} is not correctly recognized yet 12"))
              result.add(null)
            }
            SETTINGS_SUBROUTINE_LOCAL_PATH -> {
              logMessage(MessageParams(MessageType.Info, "${item.section} is not correctly recognized yet 14"))
              result.add(emptyList<String>())
            }
            SETTINGS_CICS_TRANSLATOR -> {
              logMessage(MessageParams(MessageType.Info, "${item.section} is not correctly recognized yet 15"))
              result.add("true")
            }
            else -> {
        //            result.add(cfg)
              logMessage(MessageParams(MessageType.Info, "${item.section} is not recognized yet 9"))
            }
          }
        } else {
          logMessage(MessageParams(MessageType.Info, "${item.section} is not correctly recognized yet 10"))
          result.add(emptyList<String>())
//          result.add(vscode.workspace.getConfiguration().get(item.section));
        }
      } catch (error: Throwable) {
        logMessage(MessageParams(MessageType.Error, "${error.message}\n${error.stackTrace}"))
      }
    }
    return CompletableFuture.completedFuture(result)
  }

}
