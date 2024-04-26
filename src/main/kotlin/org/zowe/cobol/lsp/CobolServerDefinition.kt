package org.zowe.cobol.lsp

import org.wso2.lsp4intellij.client.connection.StreamConnectionProvider
import org.wso2.lsp4intellij.client.languageserver.serverdefinition.RawCommandServerDefinition
import org.zowe.cobol.lsp.debug.CobolProcessStreamConnectionProvider

/** COBOL LSP server definition wrapper to provide a custom connection provider for the LSP client debug purposes */
class CobolServerDefinition(extensions: String, command: Array<String>) : RawCommandServerDefinition(extensions, command) {
  override fun createConnectionProvider(workingDir: String): StreamConnectionProvider {
    return CobolProcessStreamConnectionProvider(listOf(*command), workingDir)
  }
}