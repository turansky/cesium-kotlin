package com.github.turansky.cesium

import java.io.File

private val DEFINITION_REGEX = Regex("""\n(/\*\*\n.+?\n})""", RegexOption.DOT_MATCHES_ALL)

private val FACTORY_MAP = mapOf(
    Function.PREFIX to ::Function,
    Enum.PREFIX to ::Enum,
    Type.PREFIX to ::Type,
    "interface " to ::Interface,
    Interface.PREFIX to ::Interface,
    Class.PREFIX to ::Class,
    Namespace.PREFIX to ::Namespace
)

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
        .map { parseTopDefinition(it) }
        .map { source ->
            val body = source.body
            val prefix = FACTORY_MAP.keys
                .first { body.startsWith(it) }

            val newSource = source.copy(body = body.removePrefix(prefix))
            FACTORY_MAP.getValue(prefix)(newSource)
        }
        .sortedBy(Declaration::name)
        .forEachIndexed { index, declaration ->
            val id = index.toString()
                .let { "0".repeat(3 - it.length) + it }

            cesiumDir.resolve("${id}_${declaration.name}.kt_")
                .writeText(declaration.toCode())
        }
}
