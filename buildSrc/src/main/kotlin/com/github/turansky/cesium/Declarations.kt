package com.github.turansky.cesium

import java.io.File

private const val TS_FUNCTION = "(...params: any[]) => any"

internal const val LIGHT: String = "Light"
internal const val SPLINE: String = "Spline"
internal const val TERRAIN_DATA: String = "TerrainData"
internal const val TERRAIN_PROVIDER: String = "TerrainProvider"
internal const val TILING_SCHEME: String = "TilingScheme"
internal const val VISUALIZER: String = "Visualizer"

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

    addParentType(classMap, LIGHT)
    addParentType(classMap, TERRAIN_DATA)
    addParentType(classMap, TERRAIN_PROVIDER)
    addParentType(classMap, TILING_SCHEME)
    addParentType(classMap, VISUALIZER)

    addParentType(classMap, "TileDiscardPolicy") {
        "Tile" in it && "Discard" in it && "Policy" in it
    }

    addParentType(classMap, "StyleExpression") {
        it.endsWith("Expression")
    }

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

private fun addParentType(
    classMap: Map<String, Class>,
    parentType: String,
    filter: (String) -> Boolean = { it.endsWith(parentType) }
) {
    val abstractMembers = classMap.getValue(parentType)
        .overridableMembers()
        .map { it.name }
        .toSet()

    classMap.keys
        .asSequence()
        .filter { filter(it) && it != parentType }
        .map(classMap::getValue)
        .onEach { it.parents += parentType }
        .flatMap { it.overridableMembers() }
        .filter { it.name in abstractMembers }
        .forEach { it.overridden = true }
}

private fun Class.overridableMembers(): Sequence<Member> =
    members.asSequence()
        .filter { !it.static }
        .filter { it is Property || it is Method }

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
        .replace("[webAssemblyOptions", "[options")
        .replace("(webAssemblyOptions", "(options")
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
