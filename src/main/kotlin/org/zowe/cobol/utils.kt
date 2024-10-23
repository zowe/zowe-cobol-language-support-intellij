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

package org.zowe.cobol

import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Find all file paths that are provided in the config in the specified folder
 * @param patterns the file patterns to search for (should be as a Unix-style paths or path parts)
 * @param workspaceFolder the workspace folder path
 * @return list of file paths as strings
 */
fun findFilesInWorkspace(patterns: List<String>, workspaceFolder: Path): List<String> {
  val matchedFiles = mutableListOf<String>()

  return if (Files.isDirectory(workspaceFolder)) {
    Files.walk(workspaceFolder).use { paths ->
      paths.forEach { path ->
        patterns.forEach { pattern ->
          val matcher = FileSystems.getDefault().getPathMatcher("glob:**$pattern")
          if (matcher.matches(path)) {
            matchedFiles.add(path.toString())
          }
        }
      }
    }
    matchedFiles
  } else emptyList()
}

/**
 * Cleans the [sourceStr] from the "magic" characters (such as *?[])
 * @param sourceStr the string to clean
 * @return a cleaned string
 */
fun getStringWithoutMagic(sourceStr: String): String {
  val magicRegex = Regex("[*?\\[\\]]")
  val magicFound = magicRegex.find(sourceStr)
  val firstMagicCharIdx = magicFound?.range?.first ?: sourceStr.length
  return sourceStr.substring(0, firstMagicCharIdx)
}

/**
 * Search for a specific file by the provided parameters in the [parentFolder] path
 * @param parentFolder the parent folder path to search for the file in
 * @param resourceFolder the folder in the parent folder path to search for the file in
 * @param fileName the file name to search file by
 * @param ext the file extension to search the exact file by
 */
fun searchForFileInFolder(
  parentFolder: Path,
  resourceFolder: String,
  fileName: String,
  ext: String
): String? {
  val resourcePathStr = getStringWithoutMagic(resourceFolder)
  val resourcePath = Paths.get(resourcePathStr)
  val absResourcePath = if (resourcePath.isAbsolute) resourcePath else parentFolder.resolve(resourceFolder)
  val formedPatternPath = absResourcePath.resolve("$fileName$ext")
  val foundFiles = findFilesInWorkspace(
    listOf(formedPatternPath.toString().replace("\\", "/")),
    absResourcePath
  )
  return if (foundFiles.isNotEmpty()) foundFiles[0] else null
}
