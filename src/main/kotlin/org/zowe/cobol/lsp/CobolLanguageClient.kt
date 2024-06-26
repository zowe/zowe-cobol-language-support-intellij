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
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl
import org.eclipse.lsp4j.ConfigurationParams
import java.util.concurrent.CompletableFuture

private const val DIALECT_REGISTRY_SECTION = "cobol-lsp.dialect.registry"
private const val SETTINGS_DIALECT = "cobol-lsp.dialects"
private const val SETTINGS_CPY_LOCAL_PATH = "cobol-lsp.cpy-manager.paths-local"
private const val DIALECT_LIBS = "cobol-lsp.dialect.libs"
private const val SETTINGS_CPY_EXTENSIONS = "cobol-lsp.cpy-manager.copybook-extensions"
private const val SETTINGS_SQL_BACKEND = "cobol-lsp.target-sql-backend"
private const val SETTINGS_CPY_FILE_ENCODING = "cobol-lsp.cpy-manager.copybook-file-encoding"
private const val SETTINGS_COMPILE_OPTIONS = "cobol-lsp.compiler.options"

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
          System.err.println("${item.section} is not correctly recognized yet 1")
          result.add(emptyList<String>())
//          val computed = DialectRegistry.getDialects()
//          result.add(computed)
        } else if (item.scopeUri != "") {
//          val cfg = vscode.workspace.getConfiguration().get(item.section)
          when (item.section) {
            SETTINGS_DIALECT -> {
              System.err.println("${item.section} is not correctly recognized yet 2")
              result.add(emptyList<String>())
        //            val computed = loadProcessorGroupDialectConfig(item, cfg)
        //            result.add(computed)
            }
            SETTINGS_CPY_LOCAL_PATH -> {
              System.err.println("${item.section} is not recognized yet 3")
        //            val computed = loadProcessorGroupCopybookPathsConfig(item, cfg as List<String>)
        //            result.add(computed)
        //          } else if (item.section === DIALECT_LIBS && !!item.dialect) {
            }
            DIALECT_LIBS -> {
              System.err.println("${item.section} is not recognized yet 4")
        //            val dialectLibs = SettingsService.getCopybookLocalPath(item.scopeUri, item.dialect)
        //            result.add(dialectLibs)
            }
            SETTINGS_CPY_EXTENSIONS -> {
              System.err.println("${item.section} is not correctly recognized 5")
              result.add(listOf(".CPY", ".COPY", ".cpy", ".copy",""))
        //            val computed = loadProcessorGroupCopybookExtensionsConfig(item, cfg as List<String>)
        //            result.add(computed)
            }
            SETTINGS_SQL_BACKEND -> {
              System.err.println("${item.section} is not correctly recognized yet 6")
              result.add("DB2_SERVER")
        //            val computed = loadProcessorGroupSqlBackendConfig(item, cfg as String)
        //            result.add(computed)
            }
            SETTINGS_CPY_FILE_ENCODING -> {
              System.err.println("${item.section} is not recognized yet 7")
        //            val computed = loadProcessorGroupCopybookEncodingConfig(item, cfg as String)
        //            result.add(computed)
            }
            SETTINGS_COMPILE_OPTIONS -> {
              System.err.println("${item.section} is not correctly recognized yet 8")
              result.add(null)
        //            val computed = loadProcessorGroupCompileOptionsConfig(item, cfg as String)
        //            result.add(computed)
            }
            "cobol-lsp.logging.level.root" -> {
              System.err.println("${item.section} is not correctly recognized 11")
              result.add("ERROR")
            }
            "cobol-lsp.locale" -> {
              System.err.println("${item.section} is not correctly recognized 12")
              result.add("en")
            }
            "cobol-lsp.cobol.program.layout is not recognized yet 13" -> {
              System.err.println("${item.section} is not correctly recognized yet 12")
              result.add(null)
            }
            "cobol-lsp.subroutine-manager.paths-local" -> {
              System.err.println("${item.section} is not correctly recognized yet 14")
              result.add(emptyList<String>())
            }
            "cobol-lsp.cics.translator" -> {
              System.err.println("${item.section} is not correctly recognized yet 15")
              result.add("true")
            }
            else -> {
        //            result.add(cfg)
              System.err.println("${item.section} is not recognized yet 9")
            }
          }
        } else {
          System.err.println("${item.section} is not correctly recognized yet 10")
          result.add(emptyList<String>())
//          result.add(vscode.workspace.getConfiguration().get(item.section));
        }
      } catch (error: Throwable) {
        System.err.println(error)
      }
    }
    return CompletableFuture.completedFuture(result)
  }

}
