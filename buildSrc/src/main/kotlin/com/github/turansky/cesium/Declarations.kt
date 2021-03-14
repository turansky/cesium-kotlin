package com.github.turansky.cesium

import java.io.File

private const val TS_FUNCTION = "(...params: any[]) => any"

private val FACTORY_MAP = mapOf(
    Function.PREFIX to ::Function,
    Enum.PREFIX to ::Enum,
    TopType.PREFIX to ::TopType,
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
        .associateBy { it.name }

    // TODO: remove temp hack
    declarations.removeAll {
        it.name == "DictionaryLike"
    }

    declarations.removeAll {
        it is Interface && classMap.containsKey(it.name)
    }

    val interfaceMap = declarations.asSequence()
        .filterIsInstance<Interface>()
        .associateBy { it.name }

    declarations.removeAll {
        when {
            it !is Namespace -> false

            classMap.containsKey(it.name) -> {
                classMap.getValue(it.name).companion = it
                true
            }

            interfaceMap.containsKey(it.name) -> {
                interfaceMap.getValue(it.name).companion = it
                true
            }

            else -> false
        }
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
        .replace("($TS_FUNCTION)", JS_FUNCTION)
        .replace(TS_FUNCTION, JS_FUNCTION)
        .replace("* /**", "*")
        .replace("PropertyBag | {\n            [key: string]: any;\n        }", "PropertyBag")
        .replace("PropertyBag | {\n            [key: string]: TranslationRotationScale;\n        }", "PropertyBag")
        .replace("PropertyBag | {\n            [key: string]: number;\n        }", "PropertyBag")
        .splitToSequence("\n\n/**")
        .filter { it.isNotBlank() }
        .map { "/**$it" }
        .flatMap { it.split("\n\nexport ").asSequence() }
        .flatMap { parseTopDefinition(it) }
        .map { source ->
            val body = source.body
            val prefix = FACTORY_MAP.keys
                .first { body.startsWith(it) }

            val newSource = source.copy(body = body.removePrefix(prefix))
            FACTORY_MAP.getValue(prefix)(newSource)
        }
        .toList()
