package org.zowe.cobol.lsp

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.DocumentListener
import org.eclipse.lsp4j.SaveOptions
import org.eclipse.lsp4j.ServerCapabilities
import org.eclipse.lsp4j.TextDocumentSyncKind
import org.eclipse.lsp4j.TextDocumentSyncOptions
import org.eclipse.lsp4j.jsonrpc.messages.Either
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.services.LanguageServer
import org.wso2.lsp4intellij.client.ClientContext
import org.wso2.lsp4intellij.client.languageserver.ServerOptions
import org.wso2.lsp4intellij.client.languageserver.requestmanager.DefaultRequestManager
import org.wso2.lsp4intellij.client.languageserver.requestmanager.RequestManager
import org.wso2.lsp4intellij.client.languageserver.wrapper.LanguageServerWrapper
import org.wso2.lsp4intellij.editor.EditorEventManager
import org.wso2.lsp4intellij.extensions.LSPExtensionManager
import org.wso2.lsp4intellij.listeners.EditorMouseListenerImpl
import org.wso2.lsp4intellij.listeners.EditorMouseMotionListenerImpl
import org.wso2.lsp4intellij.listeners.LSPCaretListenerImpl

/**
 * Transform textDocumentSync [Either] option when only left value is present
 * @param serverCapabilities the server capabilities instance to get the textDocumentSync [Either] instance
 * @return the right value for the textDocumentSync [Either] instance
 */
private fun transformTextDocumentSync(
  serverCapabilities: ServerCapabilities?
): Either<TextDocumentSyncKind, TextDocumentSyncOptions> {
  val textDocumentSync = serverCapabilities?.textDocumentSync
  val textDocumentSyncOptions: TextDocumentSyncOptions? =
    if (textDocumentSync?.isRight != true) {
      when (textDocumentSync?.left) {
        TextDocumentSyncKind.Full -> {
          val textDocumentSyncOptions = TextDocumentSyncOptions()
          textDocumentSyncOptions.openClose = true
          textDocumentSyncOptions.change = textDocumentSync.left
          textDocumentSyncOptions.save = Either.forRight(SaveOptions(false))
          textDocumentSyncOptions
        }
        TextDocumentSyncKind.None -> {
          val textDocumentSyncOptions = TextDocumentSyncOptions()
          textDocumentSyncOptions.openClose = false
          textDocumentSyncOptions.change = textDocumentSync.left
          textDocumentSyncOptions
        }
        else -> null
      }
    } else textDocumentSync.right
  return Either.forRight(textDocumentSyncOptions)
}

/**
 * COBOL LSP extension manager wrapper to setup handlers for requests and events, as well as the LSP client instance
 * and LSP server interface to communicate through
 */
class CobolLSPExtensionManager : LSPExtensionManager {
  override fun <T : DefaultRequestManager?> getExtendedRequestManagerFor(
    wrapper: LanguageServerWrapper?,
    server: LanguageServer?,
    client: LanguageClient?,
    serverCapabilities: ServerCapabilities?
  ): T {
    serverCapabilities?.textDocumentSync = transformTextDocumentSync(serverCapabilities)
    return DefaultRequestManager(wrapper, server, client, serverCapabilities) as T
  }

  override fun <T : EditorEventManager?> getExtendedEditorEventManagerFor(
    editor: Editor?,
    documentListener: DocumentListener?,
    mouseListener: EditorMouseListenerImpl?,
    mouseMotionListener: EditorMouseMotionListenerImpl?,
    caretListener: LSPCaretListenerImpl?,
    requestManager: RequestManager?,
    serverOptions: ServerOptions?,
    wrapper: LanguageServerWrapper?
  ): T {
    return EditorEventManager(
      editor,
      documentListener,
      mouseListener,
      mouseMotionListener,
      caretListener,
      requestManager,
      serverOptions,
      wrapper
    ) as T
  }

  override fun getExtendedServerInterface(): Class<out LanguageServer> {
    return LanguageServer::class.java
  }

  override fun getExtendedClientFor(context: ClientContext): LanguageClient {
    return CobolLanguageClient(context)
  }
}