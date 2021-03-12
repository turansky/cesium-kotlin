package com.github.turansky.cesium

private val TOP_REGEX = Regex("""(.+?\*/)\n\s*(.+)""", RegexOption.DOT_MATCHES_ALL)
private val FUN_START_REGEX = Regex("""^(static )?[\w\d]+\(""")

private val MULTI_TYPES = listOf(
    "Resource | string | Document | Blob",
    "Resource | string | ArrayBuffer",
    "Resource | string | any",
    "Resource | string",

    "HTMLImageElement | HTMLCanvasElement | string | Resource | Billboard.CreateImageCallback",
    "PostProcessStage | PostProcessStageComposite",
    "Cartesian3 | HeadingPitchRange",
    "DataSource | Promise<DataSource>",
    "KmlTourFlyTo | KmlTourWait",

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

internal fun Definition.flatten(): Sequence<Definition> {
    if (!body.startsWith("export function ") && FUN_START_REGEX.find(body) == null) {
        return sequenceOf(this)
    }

    val multiType = MULTI_TYPES
        .firstOrNull { (": $it," in body || ": $it)" in body) && "?: $it" !in body }
        ?: return sequenceOf(this)

    return multiType.splitToSequence(" | ")
        .mapIndexed { index, type ->
            Definition(
                doc = if (index == 0) doc else "",
                body = body.replace(multiType, type)
            )
        }
}
