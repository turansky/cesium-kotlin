package com.github.turansky.cesium

private val TOP_REGEX = Regex("""(.+?\*/)\n\s*(.+)""", RegexOption.DOT_MATCHES_ALL)

interface HasDoc {
    val doc: String
}

data class Definition(
    override val doc: String,
    val body: String
) : HasDoc

fun parseTopDefinition(
    data: String
): Definition {
    val source = data.trim().removeSuffix(";")

    return if (source.startsWith("/**")) {
        TOP_REGEX.find(source)!!
            .groupValues
            .let { Definition(it[1], it[2]) }
    } else {
        Definition("", source)
    }
}
