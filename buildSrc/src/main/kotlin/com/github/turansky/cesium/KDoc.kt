package com.github.turansky.cesium

internal fun kdoc(doc: String): String {
    if (doc.isEmpty())
        return ""

    val source = doc.removePrefix("/**\n")
        .substringBeforeLast("\n")
        .trimMargin("*")
        .splitToSequence("\n")
        .map { it.removePrefix(" ") }
        .joinToString("\n")

    return source
        .splitToSequence("\n")
        .map { " * $it" }
        .joinToString(
            prefix = "/**\n",
            separator = "\n",
            postfix = "\n */"
        )
}
