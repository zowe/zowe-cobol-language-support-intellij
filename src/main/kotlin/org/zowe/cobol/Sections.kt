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

/**
 * Various config section options that could be found across configuration files
 * @property section the section string representation
 */
enum class Sections(val section: String) {
  DIALECTS_SECTION("cobol-lsp.dialects"),
  DIALECT_REGISTRY("cobol-lsp.dialect.registry"),
  DIALECT_LIBS("cobol-lsp.dialect.libs"),
  CPY_SECTION("cobol-lsp.cpy-manager"),
  CPY_LOCAL_PATH("cobol-lsp.cpy-manager.paths-local"),
  CPY_EXTENSIONS("cobol-lsp.cpy-manager.copybook-extensions"),
  CPY_FILE_ENCODING("cobol-lsp.cpy-manager.copybook-file-encoding"),
  SQL_BACKEND("cobol-lsp.target-sql-backend"),
  COMPILER_OPTIONS("cobol-lsp.compiler.options"),
  LOGGIN_LEVEL_ROOT("cobol-lsp.logging.level.root"),
  LOCALE("cobol-lsp.locale"),
  COBOL_PROGRAM_LAYOUT("cobol-lsp.cobol.program.layout"),
  SUBROUTINE_LOCAL_PATH("cobol-lsp.subroutine-manager.paths-local"),
  CICS_TRANSLATOR("cobol-lsp.cics.translator"),
  UNRECOGNIZED("unrecognized-section");

  companion object {
    operator fun invoke(section: String): Sections {
      return entries.find { it.section == section } ?: UNRECOGNIZED
    }
  }

  override fun toString(): String {
    return section
  }
}