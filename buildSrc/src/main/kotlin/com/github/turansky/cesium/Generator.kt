package com.github.turansky.cesium

import java.io.File

internal fun generateKotlinDeclarations(
    definitionsFile: File,
    sourceDir: File
) {
    val cesiumDir = sourceDir.resolve("cesium")
        .also { it.mkdirs() }

    parseDeclarations(definitionsFile)
        .asSequence()
        .sortedBy(Declaration::name)
        .forEach { declaration ->
            val file = cesiumDir.resolve("${declaration.name}.kt")
            if (!file.exists()) {
                file.writeText(declaration.toCode())
            } else {
                // for functions with union type parameters
                file.appendText("\n\n" + declaration.toCode())
            }
        }
}
