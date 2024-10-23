# Zowe™ COBOL Language Support plug-in for IntelliJ IDEA™

Provides: 
- syntax highlighting support using TextMate bundle from [eclipse-che4z/che-che4z-lsp-for-cobol](https://github.com/eclipse-che4z/che-che4z-lsp-for-cobol)
- code actions using LSP technology with client from [redhat-developer/lsp4ij](https://github.com/redhat-developer/lsp4ij) and server from [eclipse-che4z/che-che4z-lsp-for-cobol](https://github.com/eclipse-che4z/che-che4z-lsp-for-cobol)

## Supported features

### Syntax Highlighting and Coloring

The feature allows to inspect COBOL sources with highlighted instructions, recognized as COBOL language elements, colored respective to their type.

### Syntax and Semantic Code Check

The plug-in walks through the content of COBOL source files and checks it for mistakes and errors, highlighting it respectively with suggestions on how to fix them. 

### Code Autocompletion

The feature provides a functionality to autocomplete instructions, suggesting the possible options to complete the words being typed (works for .cob/.cbl files, not for copybooks).

### Copybooks Recognition

The plug-in recognizes local copybooks, used in COBOL sources. The .cpy/.copy files content is highlighted as COBOL source code.
To use local copybooks:
1. Create **.vscode** folder in your opened workspace
2. Create **settings.json** in the **.vscode** folder
3. Enter relative or absolute paths of the folders, where copybooks are placed

Example of the **settings.json** content:
```json5
{
  //...
  "cobol-lsp.cpy-manager.paths-local": [
    ".copybooks/zowe-profile-1/DATA.SET.PATH1",
    ".copybooks/some-path",
    "/some/absolute/path"
  ],
  //...
}
```

The plug-in will search through the paths to find related copybook files. Using "Go To Definition" functionality will open the found copybook.

## Prerequisites

- Java v17
- IntelliJ v2023.2 or later

## How to run (user)

- Open the folder with the project, run `./gradlew buildPlugin` (for Unix-like) or `.\gradlew.bat buildPlugin` (for Windows) to build the plugin (or run "Package plugin" configuration)
- The built plug-in will be at the `build/distributions` in .zip format, install it with Settings -> Plugins -> Install plugin from disk
- Reload your IDE

## How to run (developer)

- Open the folder with the project, run "Run plugin" configuration, wait for the other instance of IDE to run

or

- Download latest [GitHub Actions build](https://github.com/zowe/zowe-cobol-language-support-intellij/actions/workflows/build.yml)
