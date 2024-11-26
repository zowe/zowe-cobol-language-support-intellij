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
import org.zowe.cobol.searchForFileInPath
import java.nio.file.Path
import java.nio.file.Paths

/** Service to handle the functionalities around copybooks */
@Service
class CobolCopybooksService {

  companion object {
    fun getService(): CobolCopybooksService = service()
  }

  // TODO: doc
  // TODO: implement
  // TODO: resolve the actual functionality
//  private fun getTargetFolderForCopybook(
//  folderKind: string | CopybookFolderKind,
//  documentUri: string,
//  dialectType: string,
//  ) {
//    let result: string[] = [];
//    const profile = SettingsService.getProfileName()!;
//    switch (folderKind) {
//      case CopybookFolderKind[CopybookFolderKind.local]:
//      result = SettingsService.getCopybookLocalPath(documentUri, dialectType);
//      break;
//      case CopybookFolderKind[CopybookFolderKind["downloaded-dsn"]]:
//      result = SettingsService.getDsnPath(documentUri, dialectType).map(
//        (dnsPath) => CopybookURI.createDatasetPath(profile, dnsPath),
//      );
//      break;
//      case CopybookFolderKind[CopybookFolderKind["downloaded-uss"]]:
//      result = SettingsService.getUssPath(documentUri, dialectType).map(
//        (dnsPath) => CopybookURI.createDatasetPath(profile, dnsPath),
//      );
//      break;
//    }
//    return result;
//  }

  // TODO: doc
  // TODO: implement
  // TODO: resolve the actual functionality
//  private fun searchCopybook(
//    documentUri: String,
//    copybookName: String,
//    dialectType: String
//  ) {
//    let result: string | undefined;
//    for (let i = 0; i < Object.values(CopybookFolderKind).length; i++) {
//      const folderKind = Object.values(CopybookFolderKind)[i];
//      const targetFolder = getTargetFolderForCopybook(
//        folderKind,
//      documentUri,
//      dialectType,
//      );
//      const allowedExtensions = resolveAllowedExtensions(folderKind, documentUri);
//      result = searchCopybookInWorkspace(
//        copybookName,
//        targetFolder,
//        allowedExtensions,
//      );
//      if (result) {
//        return result;
//      }
//    }
//    return result;
//  }

  /**
   * Search for the specified copybook in the workspace folder
   * @param workspaceFolder the workspace folder path to search for the copybook in
   * @param copybookName the name of the copybook to search for
   * @param copybookFolders the copybook folders in the workspace to search for the copybook in
   * @param extensions the potential copybook extensions to search for the exact copybook by
   * @return the found copybook path string or null
   */
  private fun searchCopybookInWorkspace(
    workspaceFolder: Path,
    copybookName: String,
    copybookFolders: List<String>,
    extensions: List<String>
  ): String? {
    if (copybookName.isEmpty() || extensions.isEmpty()) return null
    for (copybookFolder in copybookFolders) {
      for (ext in extensions) {
        val foundCopybookPath = searchForFileInPath(workspaceFolder, copybookFolder, copybookName, ext)
        if (foundCopybookPath != null) return Paths.get(foundCopybookPath).toUri().toString()
      }
    }
    return null
  }

  // TODO: finalize the functionality when interaction with Zowe Explorer is set up
  /**
   * Resolve a path of the provided copybook
   * @param workspaceFolder the workspace folder path
   * @param copybookFolders the folders containing the copybooks
   * @param copybookName the name of the copybook to search for
   * @param copybookExtensions the potential copybook extension to search for the exact copybook
   * @return the copybook path in a URI string style
   */
  fun resolveCopybookPath(
    workspaceFolder: Path,
    copybookFolders: List<String>,
    copybookName: String,
    copybookExtensions: List<String>
  ): String? {
//    val copybook = searchCopybook(documentUri, copybookName, dialectType);
    val result = searchCopybookInWorkspace(workspaceFolder, copybookName, copybookFolders, copybookExtensions)
    return result
  }

}
