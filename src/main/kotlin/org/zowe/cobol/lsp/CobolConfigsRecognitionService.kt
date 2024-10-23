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

import com.google.gson.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import org.eclipse.lsp4j.ConfigurationItem
import org.zowe.cobol.findFilesInPath
import java.io.File
import java.lang.reflect.Type
import java.net.URI
import java.nio.file.*

private const val PROCESSOR_GROUP_FOLDER = ".cobolplugin"
private const val PROCESSOR_GROUP_PGM = "pgm_conf.json"
private const val PROCESSOR_GROUP_PROC = "proc_grps.json"
private val COBOL_EXTENSTIONS = listOf(".CBL", ".COB", ".COBOL")

/** Service to handle COBOL-related configs */
@Service
class CobolConfigsRecognitionService {

  companion object {
    fun getService(): CobolConfigsRecognitionService = service()
  }

  /**
   * Preprocessor description item class
   * @property name the name of the preprocessor
   * @property libs the optional preprocessor libs
   */
  private data class PreprocessorItem(val name: String, val libs: List<String> = emptyList())

  /**
   * Processor group class to represent processor groups
   * @property name the name of the processor group
   * @property libs the optional libs to be used
   * @property preprocessor the optional preprocessors list to be used with the processor group
   */
  private data class ProcessorGroup(
    val name: String,
    val libs: List<String> = emptyList(),
    val preprocessor: List<PreprocessorItem> = emptyList()
  )

  /**
   * Processor groups container class
   * @property pgroups the processor groups list
   */
  private data class PGroupContainer(val pgroups: List<ProcessorGroup>)

  /** Class to provide a deserialization mechanism for processor groups */
  private class ProcessorGroupDeserializer : JsonDeserializer<ProcessorGroup> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ProcessorGroup {
      val jsonObject = json.asJsonObject

      val name = jsonObject.get("name").asString
      val libs = jsonObject.getAsJsonArray("libs")?.map { it.asString } ?: emptyList()

      val preprocessor = when {
        jsonObject.has("preprocessor") -> {
          val preprocessorElement = jsonObject.get("preprocessor")
          if (preprocessorElement.isJsonArray) {
            preprocessorElement.asJsonArray.map { parsePreprocessorItem(it) }
          } else {
            listOf(parsePreprocessorItem(preprocessorElement))
          }
        }
        else -> emptyList()
      }

      return ProcessorGroup(name, libs, preprocessor)
    }

    /**
     * Parse preprocessor item from JSON. It is either a string or an object
     * @param element the [JsonElement] to parse preprocessor item from
     * @return parsed and formed [PreprocessorItem] instance
     */
    private fun parsePreprocessorItem(element: JsonElement): PreprocessorItem {
      return if (element.isJsonPrimitive && element.asJsonPrimitive.isString) {
        PreprocessorItem(name = element.asString)
      } else {
        val jsonObject = element.asJsonObject
        val name = jsonObject.get("name").asString
        val libs = jsonObject.getAsJsonArray("libs")?.map { it.asString } ?: emptyList()
        PreprocessorItem(name = name, libs = libs)
      }
    }
  }

  /**
   * Program config item class to represent a single COBOL program configuration
   * @param program a name or a wildcard of a file to be considered as the main program (open code)
   * @param pgroup name of a processor group as defined in proc_grps.json
   */
  private data class ProgramItem(val program: String, val pgroup: String)

  /**
   * Programs config class to represent program configuration options for the COBOL programs
   * @param pgms a list of main programs
   */
  private data class ProgramContainer(val pgms: List<ProgramItem>)

  // TODO: recheck functionality correctness
  /**
   * Match provided processor group path from the deserialized JSON with the actual document path
   * @param pgmCfg the [ProgramContainer] config item to find the proc group path that matches with the document path
   * @param documentPathStr the document path as a string
   * @param workspaceFolder the workspace folder to find the paths match in
   * @return the matched processor group name or null
   */
  private fun matchProcessorGroup(pgmCfg: ProgramContainer, documentPathStr: String, workspaceFolder: Path): String? {
    val documentPath = Paths.get(documentPathStr)
    val relativeDocPath = workspaceFolder.relativize(documentPath)

    val candidates = mutableListOf<String>()

    for ((program, pgroup) in pgmCfg.pgms) {
      val programPath = try {
        Paths.get(program)
      } catch (e: InvalidPathException) {
        null
      }
      // exact match
      if (programPath != null && programPath.isAbsolute) {
        if (
          programPath == documentPath
          || programPath.toString().uppercase() == documentPath.toString().uppercase()
        ) {
          return pgroup
        }
      } else {
        if (relativeDocPath == programPath) {
          candidates.add(pgroup)
        }
      }

      val relativeDocPathIgnoreCase = Paths.get(relativeDocPath.toString().uppercase())
      val matcher = FileSystems.getDefault().getPathMatcher("glob:${program.uppercase()}")
      if (matcher.matches(relativeDocPathIgnoreCase)) {
        candidates.add(pgroup)
      }
    }

    return candidates.getOrNull(0)
  }

  /**
   * Load processors config for the related document
   * @param workspaceFolder the workspace folder to load configs in
   * @param documentUri the document to find config for
   * @return the [ProcessorGroup] config or null
   */
  private fun loadProcessorsConfig(workspaceFolder: Path, documentUri: URI): ProcessorGroup? {
    val documentPath = Paths.get(documentUri).toString()
    val cfgPath = workspaceFolder.resolve(PROCESSOR_GROUP_FOLDER)
    val procCfgPath = cfgPath.resolve(PROCESSOR_GROUP_PROC)
    val pgmCfgPath = cfgPath.resolve(PROCESSOR_GROUP_PGM)
    if (!Files.exists(pgmCfgPath) || !Files.exists(procCfgPath)) {
      return null
    }
    val procCfgFile = File(procCfgPath.toUri()).readText()
    val procCfgGson = GsonBuilder()
      .registerTypeAdapter(ProcessorGroup::class.java, ProcessorGroupDeserializer())
      .create()
    val procCfg = procCfgGson.fromJson(procCfgFile, PGroupContainer::class.java)

    val pgmCfgFile = File(pgmCfgPath.toUri()).readText()
    val pgmCfgGson = Gson()
    val pgmCfg = pgmCfgGson.fromJson(pgmCfgFile, ProgramContainer::class.java)

    val pgroup = matchProcessorGroup(pgmCfg, documentPath, workspaceFolder)

    val result = procCfg.pgroups.find { p -> pgroup == p.name }
    return result
  }

  // TODO: doc
  fun loadProcessorGroupDialectConfig(
    workspaceFolder: Path,
    item: ConfigurationItem,
    configObject: List<String>
  ): List<String> {
    return try {
      // "SQL" is not a real dialect, we will use it only to set up sql backend for now
      loadProcessorsConfig(workspaceFolder, URI(item.scopeUri))
        ?.preprocessor
        ?.map { (name, _) -> name }
        ?.filter { name -> name != "SQL" }
        ?.ifEmpty { configObject }
        ?: configObject
    } catch (e: Exception) {
      println(e)
      configObject
    }
  }

  /**
   * Load copybook paths from the related to the [item] processor config
   * @param workspaceFolder the workspace folder to find and load config in
   * @param item the configuration item to find the related processor configs for
   * @param configObject the config object to return as a default configuration if no specific configs found
   * @return the related copybook paths
   */
  fun loadProcessorGroupCopybookPathsConfig(
    workspaceFolder: Path,
    item: ConfigurationItem,
    configObject: List<String>
  ): List<String> {
//    val copybookPaths = loadProcessorsConfig(workspaceFolder, URI(item.scopeUri))
//      ?.libs
//      ?.ifEmpty { configObject }
//      ?: configObject
    val copybookPaths = configObject
    return findFilesInPath(copybookPaths, workspaceFolder)
  }

  // TODO: doc
  // TODO: implement
//  fun resolveSubroutineURI(project: Project, name: String): String {
//    val folders = VSCodeSettingsAdapterService
//      .getService(project)
//      .getListOfStringsConfiguration(Sections.SUBROUTINE_LOCAL_PATH)
//    return searchCopybookInWorkspace(project, name, folders, COBOL_EXTENSTIONS)
//  }

}