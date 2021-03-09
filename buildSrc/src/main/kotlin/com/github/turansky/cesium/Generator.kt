package com.github.turansky.cesium

import java.io.File

private val DEFINITION_REGEX = Regex("""\n(/\*\*\n.+?\n})""", setOf(RegexOption.DOT_MATCHES_ALL))

internal fun generateKotlinDeclarations(
    definitionsFile: File,
    sourceDir: File
) {
    val cesiumDir = sourceDir.resolve("cesium")
        .also { it.mkdirs() }

    val definitions = definitionsFile.readText()
        .replace("\n}/**", "\n}\n\n/**")

    DEFINITION_REGEX.findAll(definitions)
        .map { it.groupValues[1] }
        .forEachIndexed { index, value ->
            val id = index.toString()
                .let { "0".repeat(3 - it.length) + it }

            cesiumDir.resolve("$id.kt_")
                .writeText(value)

            println("File: $id")
        }
}
