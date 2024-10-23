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
import org.eclipse.lsp4j.*
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest
import org.zowe.cobol.Sections
import org.zowe.cobol.VSCodeSettingsAdapterService
import java.net.URI
import java.util.concurrent.CompletableFuture
import kotlin.io.path.toPath

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
      val section = Sections(item.section)
      try {
        if (section == Sections.DIALECT_REGISTRY) {
          logMessage(MessageParams(MessageType.Info, "${item.section} is not correctly recognized yet 1"))
          result.add(emptyList<String>())
//          val dialectInfos = DialectsService.getService().getDialects()
//          logMessage(MessageParams(MessageType.Info, "Registered dialects: $dialectInfos"))
//          result.add(dialectInfos)
        } else if (item.scopeUri != null && item.scopeUri != "") {
          val workspaceFolders = this.workspaceFolders().get().map { pathObj -> URI.create(pathObj.uri).toPath() }
//          val cfg = vscode.workspace.getConfiguration().get(item.section)
          when (section) {
            Sections.DIALECTS_SECTION -> {
              logMessage(MessageParams(MessageType.Info, "${item.section} is not correctly recognized yet 2"))
              result.add(emptyList<String>())
//              val settingsCfg = VSCodeSettingsAdapterService.getService()
//                .getListOfStringsConfiguration(workspaceFolders[0], Sections.DIALECTS_SECTION)
//              val dialects = CobolConfigsRecognitionService.getService()
//                .loadProcessorGroupDialectConfig(workspaceFolders[0], item, settingsCfg)
//              logMessage(MessageParams(MessageType.Info, "For ${item.scopeUri} using dialects: $dialects"))
//              result.add()
//              result.add(dialects)
            }

            Sections.CPY_LOCAL_PATH -> {
              val settingsCfg = VSCodeSettingsAdapterService.getService()
                .getListOfStringsConfiguration(workspaceFolders[0], Sections.CPY_LOCAL_PATH)
              val cpyLocalPaths = CobolConfigsRecognitionService.getService()
                .loadProcessorGroupCopybookPathsConfig(workspaceFolders[0], item, settingsCfg)
              logMessage(
                MessageParams(MessageType.Info, "For ${item.scopeUri} using cpy local paths: $cpyLocalPaths")
              )
              result.add(cpyLocalPaths)
            }

            Sections.DIALECT_LIBS -> {
              logMessage(MessageParams(MessageType.Info, "$section is not recognized yet 3"))
              //            val dialectLibs = SettingsService.getCopybookLocalPath(item.scopeUri, item.dialect)
              //            result.add(dialectLibs)
            }

            Sections.CPY_EXTENSIONS -> {
              logMessage(MessageParams(MessageType.Info, "$section is not correctly recognized yet 4"))
              result.add(listOf(".CPY", ".COPY", ".cpy", ".copy", ""))
              //            val computed = loadProcessorGroupCopybookExtensionsConfig(item, cfg as List<String>)
              //            result.add(computed)
            }

            Sections.SQL_BACKEND -> {
              logMessage(MessageParams(MessageType.Info, "$section is not correctly recognized yet 5"))
              result.add("DB2_SERVER")
              //            val computed = loadProcessorGroupSqlBackendConfig(item, cfg as String)
              //            result.add(computed)
            }

            Sections.CPY_FILE_ENCODING -> {
              logMessage(MessageParams(MessageType.Info, "$section is not recognized yet 6"))
              //            val computed = loadProcessorGroupCopybookEncodingConfig(item, cfg as String)
              //            result.add(computed)
            }

            Sections.COMPILER_OPTIONS -> {
              logMessage(MessageParams(MessageType.Info, "$section is not correctly recognized yet 7"))
              result.add(null)
              //            val computed = loadProcessorGroupCompileOptionsConfig(item, cfg as String)
              //            result.add(computed)
            }

            Sections.LOGGIN_LEVEL_ROOT -> {
              logMessage(MessageParams(MessageType.Info, "$section is not correctly recognized yet 8"))
              result.add("ERROR")
            }

            Sections.LOCALE -> {
              logMessage(MessageParams(MessageType.Info, "$section is not correctly recognized yet 9"))
              result.add("en")
            }

            Sections.COBOL_PROGRAM_LAYOUT -> {
              logMessage(MessageParams(MessageType.Info, "$section is not correctly recognized yet 10"))
              result.add(null)
            }

            Sections.SUBROUTINE_LOCAL_PATH -> {
              logMessage(MessageParams(MessageType.Info, "$section is not correctly recognized yet 11"))
              result.add(emptyList<String>())
              //
            }

            Sections.CICS_TRANSLATOR -> {
              logMessage(MessageParams(MessageType.Info, "$section is not correctly recognized yet 12"))
              result.add("true")
            }

            else -> {
              //            result.add(cfg)
              logMessage(MessageParams(MessageType.Info, "${item.section} is not recognized yet 13"))
            }
          }
        } else {
          logMessage(MessageParams(MessageType.Info, "${item.section} is not correctly recognized yet 14"))
          result.add(emptyList<String>())
//          result.add(vscode.workspace.getConfiguration().get(item.section))
        }
      } catch (error: Throwable) {
        logMessage(MessageParams(MessageType.Error, "${error.message}\n${error.stackTrace}"))
      }
    }
    return CompletableFuture.completedFuture(result)
  }

  /**
   * Handle the "copybook/resolve" request
   * @param documentUri the document uri string, that triggered the request
   * @param copybookName the copybook name to resolve
   * @param dialectType the dialect type to resolve the copybook with
   * @return the URI path string to the resolved copybook
   */
  @JsonRequest("copybook/resolve")
  fun resolveCopybook(documentUri: String, copybookName: String, dialectType: String): CompletableFuture<String?> {
    val cpyExtenstionsConfigItem = ConfigurationItem()
    cpyExtenstionsConfigItem.section = Sections.CPY_EXTENSIONS.toString()
    cpyExtenstionsConfigItem.scopeUri = documentUri

    val cpyLocalPathsConfigItem = ConfigurationItem()
    cpyLocalPathsConfigItem.section = Sections.CPY_LOCAL_PATH.toString()
    cpyLocalPathsConfigItem.scopeUri = documentUri

    val configParams = ConfigurationParams(listOf(cpyExtenstionsConfigItem, cpyLocalPathsConfigItem))

    return this.workspaceFolders()
      .thenCombine<MutableList<Any?>?, String?>(this.configuration(configParams)) { workspaceFolders, cpyConfigsAny ->
        val workspaceFolder = URI.create(workspaceFolders[0].uri).toPath()

        val cpyConfigs = cpyConfigsAny as List<List<String>>
        val cpyExtensions = cpyConfigs[0]
        val cpyLocalPaths = cpyConfigs[1]

        CobolCopybooksService.getService()
          .resolveCopybookPath(workspaceFolder, cpyLocalPaths, copybookName, cpyExtensions)
      }
  }

// TODO: implement custom requests
//  @JsonRequest("cobol/resolveSubroutine")
//  @JsonRequest("copybook/download")

}
