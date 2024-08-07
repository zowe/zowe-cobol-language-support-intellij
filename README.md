# Zowe™ COBOL Language Support plug-in for IntelliJ IDEA™

Provides: 
- syntax highlighting support using TextMate bundle from [eclipse-che4z/che-che4z-lsp-for-cobol](https://github.com/eclipse-che4z/che-che4z-lsp-for-cobol)
- code actions using LSP technology with client from [redhat-developer/lsp4ij](https://github.com/redhat-developer/lsp4ij) and server from [eclipse-che4z/che-che4z-lsp-for-cobol](https://github.com/eclipse-che4z/che-che4z-lsp-for-cobol)

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
