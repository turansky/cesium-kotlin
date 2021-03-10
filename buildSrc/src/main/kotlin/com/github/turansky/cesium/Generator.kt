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
            val id = index.toString()
                .let { "0".repeat(3 - it.length) + it }

            val name = when (declaration) {
                is Enum,
                is TopType,
                is Class
                -> "${declaration.fileName}.kt"

                else -> "${id}_${declaration.fileName}.kt_"
            }

            cesiumDir.resolve(name)
                .writeText(declaration.toCode())
        }
}
