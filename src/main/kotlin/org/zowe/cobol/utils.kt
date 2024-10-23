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
import java.util.stream.Stream

/**
 * Find related files in the provided paths
 * @param patterns the patterns to search for the files by
 * @param paths the paths to search for the files in
 * @return list of path strings
 */
fun findRelatedFilesInPaths(patterns: List<String>, paths: Stream<Path>): List<String> {
  return paths.toList().fold(mutableListOf()) { resultFiles, nextPath ->
    val nextResultFiles = patterns.filter { pattern ->
      val matcher = FileSystems.getDefault().getPathMatcher("glob:**$pattern")
      matcher.matches(nextPath)
    }
    resultFiles.addAll(nextResultFiles)
    resultFiles
  }
}

/**
 * Find all file paths that are provided in the config in the specified path
 * @param patterns the file patterns to search for (should be as a Unix-style paths or path parts)
 * @param pathToSearchBy the path to search in the patterns by
 * @return list of file paths as strings
 */
fun findFilesInPath(patterns: List<String>, pathToSearchBy: Path): List<String> {
  val matchedFiles: MutableList<String> = mutableListOf()
  Files.walk(pathToSearchBy).use { paths ->
    matchedFiles.addAll(findRelatedFilesInPaths(patterns, paths))
  }
  return matchedFiles
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
 * @param resource the folder of file in the parent folder path to search for the file in
 * @param fileName the file name to search file by
 * @param ext the file extension to search the exact file by
 */
fun searchForFileInPath(
  parentFolder: Path,
  resource: String,
  fileName: String,
  ext: String
): String? {
  val resourcePathStr = getStringWithoutMagic(resource)
  val resourcePath = Paths.get(resourcePathStr)
  val absResourcePath = if (resourcePath.isAbsolute) resourcePath else parentFolder.resolve(resource)
  val fileAbsPathRegex = ".*$fileName\\$ext$".toRegex()
  val foundPaths = if (fileAbsPathRegex.matches(absResourcePath.toString())) {
    listOf(absResourcePath.toString())
  } else {
    val formedPatternPath = absResourcePath.resolve("$fileName$ext")
    findFilesInPath(
      listOf(formedPatternPath.toString().replace("\\", "/")),
      absResourcePath
    )
  }
  return if (foundPaths.isNotEmpty()) foundPaths[0] else null
}
