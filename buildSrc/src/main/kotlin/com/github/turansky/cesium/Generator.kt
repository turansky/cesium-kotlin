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
        .forEachIndexed { index, declaration ->
            val name = when (declaration) {
                !is Interface
                -> "${declaration.fileName}.kt"

                else -> "${declaration.fileName}.kt_"
            }

            cesiumDir.resolve(name)
                .writeText(declaration.toCode())
        }
}
