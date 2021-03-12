package com.github.turansky.cesium

private val TOP_REGEX = Regex("""(.+?\*/)\n\s*(.+)""", RegexOption.DOT_MATCHES_ALL)
private val FUN_START_REGEX = Regex("""^(static )?[\w\d]+\(""")

private val MULTI_TYPES = listOf(
    "Resource | string | Document | Blob",
    "Resource | string | ArrayBuffer",
    "Resource | string | any",
    "Resource | string",

    "Entity | Entity[] | EntityCollection | DataSource | ImageryLayer | Cesium3DTileset | TimeDynamicPointCloud" +
            " | Promise<Entity | Entity[] | EntityCollection | DataSource | ImageryLayer | Cesium3DTileset | TimeDynamicPointCloud>",

    "HTMLImageElement | HTMLCanvasElement | string | Resource | Billboard.CreateImageCallback",
    "PostProcessStage | PostProcessStageComposite",
    "DataSource | Promise<DataSource>",

    "Cartesian3 | HeadingPitchRange",
    "Property | Color",
    "DataSource | CompositeEntityCollection",

    "KmlTourFlyTo | KmlTourWait",
    "KmlCamera | KmlLookAt",

    "ImageryProvider | TerrainProvider",
    "Promise<object> | any",

    "number | Packable",
    "string | number"
)

internal interface HasDoc {
    val doc: String
}

internal data class Definition(
    override val doc: String,
    val body: String
) : HasDoc

internal fun parseTopDefinition(
    data: String
): Sequence<Definition> {
    val source = data.trim().removeSuffix(";")

    return if (source.startsWith("/**")) {
        TOP_REGEX.find(source)!!
            .groupValues
            .let { Definition(it[1], it[2]) }
    } else {
        Definition("", source)
    }.flatten()
}

private fun Definition.flatten(): Sequence<Definition> {
    if (!body.startsWith("export function ") && FUN_START_REGEX.find(body) == null) {
        return sequenceOf(this)
    }

    val multiType = MULTI_TYPES
        .firstOrNull { (": $it," in body || ": $it)" in body) && "?: $it" !in body }
        ?: return sequenceOf(this)

    return multiType.containedTypes()
        .mapIndexed { index, type ->
            Definition(
                doc = if (index == 0) doc else "",
                body = body.replace(multiType, type)
            )
        }
}

private fun String.containedTypes(): Sequence<String> {
    val promiseTypes = substringAfter(" | Promise<").substringBefore(">")
    if (promiseTypes == this || " | " !in promiseTypes)
        return splitToSequence(" | ")

    return substringBefore(" | Promise<")
        .splitToSequence(" | ") +
            promiseTypes.splitToSequence(" | ")
                .map { "Promise<$it>" }
}
