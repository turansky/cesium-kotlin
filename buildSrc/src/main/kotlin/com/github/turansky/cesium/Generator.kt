package com.github.turansky.cesium

import java.io.File

private const val MODULE_ANNOTATION: String = """@file:JsModule("cesium")"""

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
            val code = declaration.toCode()
            if (!file.exists()) {
                val content = if ("\nexternal " in code) {
                    MODULE_ANNOTATION + "\n\n" + code
                } else code

                file.writeText(content)
            } else {
                // for functions with union type parameters
                file.appendText("\n\n" + code)
            }
        }
}
