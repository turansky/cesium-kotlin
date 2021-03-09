package com.github.turansky.cesium

import java.io.File

private val FACTORY_MAP = mapOf(
    Function.PREFIX to ::Function,
    Enum.PREFIX to ::Enum,
    Type.PREFIX to ::Type,
    "interface " to ::Interface,
    Interface.PREFIX to ::Interface,
    Class.PREFIX to ::Class,
    "namespace " to ::Namespace,
    Namespace.PREFIX to ::Namespace
)

internal fun generateKotlinDeclarations(
    definitionsFile: File,
    sourceDir: File
) {
    val cesiumDir = sourceDir.resolve("cesium")
        .also { it.mkdirs() }

    definitionsFile.readText()
        .replace("\n}/**", "\n}\n\n/**")
        .removePrefix("""declare module "cesium" {""")
        .substringBefore("\n\n\n\n}")
        .split("\n\n/**")
        .filter { it.isNotBlank() }
        .asSequence()
        .map { "/**$it" }
        .flatMap { it.split("\n\nexport ").asSequence() }
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
