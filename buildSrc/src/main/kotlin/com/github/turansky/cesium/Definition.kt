package com.github.turansky.cesium

private val TOP_REGEX = Regex("""(.+?\*/)\n\s*(.+)""", RegexOption.DOT_MATCHES_ALL)

private val MULTI_PARAMETERS = listOf(
    "Resource | string | ArrayBuffer",
    "string | number",
    "HTMLImageElement | HTMLCanvasElement | string | Resource | Billboard.CreateImageCallback",
    "Cartesian3 | HeadingPitchRange",
    "Resource | string",
    "Resource | string | any",
    "DataSource | Promise<DataSource>",
    "Resource | string | Document | Blob",
    "KmlTourFlyTo | KmlTourWait"
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
            .flatten()
    } else {
        sequenceOf(Definition("", source))
    }
}

internal fun Definition.flatten(): Sequence<Definition> {
    return sequenceOf(this)
}
