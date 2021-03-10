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

internal fun parseDeclarations(
    definitionsFile: File
): List<Declaration> {
    val declarations = readDeclarations(definitionsFile)
        .toMutableList()

    val classMap = declarations.asSequence()
        .filterIsInstance<Class>()
        .associateBy { it.fileName }

    declarations.removeAll {
        it is Interface && classMap.containsKey(it.fileName)
    }

    return declarations
}

private fun readDeclarations(
    definitionsFile: File
): List<Declaration> =
    definitionsFile.readText()
        .replace("\n}/**", "\n}\n\n/**")
        .removePrefix("""declare module "cesium" {""")
        .substringBefore("\n\n\n\n}")
        .splitToSequence("\n\n/**")
        .filter { it.isNotBlank() }
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
        .toList()
