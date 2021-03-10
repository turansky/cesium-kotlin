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
        .sortedBy(Declaration::fileName)
        .forEach { declaration ->
            cesiumDir.resolve("${declaration.fileName}.kt")
                .also { check(!it.exists()) }
                .writeText(declaration.toCode())
        }
}
