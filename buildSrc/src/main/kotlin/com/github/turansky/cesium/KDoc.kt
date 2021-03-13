package com.github.turansky.cesium

private val DIV_REGEX = Regex("""\n<div .+?\n</div>""", RegexOption.DOT_MATCHES_ALL)
private val SPAN_1_REGEX = Regex("""\n<span .+?</span>""", RegexOption.DOT_MATCHES_ALL)
private val SPAN_2_REGEX = Regex("""<span .+?</span>\n""", RegexOption.DOT_MATCHES_ALL)

internal fun kdoc(doc: String): String {
    if (doc.isEmpty())
        return ""

    val source = doc.removePrefix("/**\n")
        .substringBeforeLast("\n")
        .trimMargin("*")
        .splitToSequence("\n")
        .map { it.removePrefix(" ") }
        .joinToString("\n")
        .replace(DIV_REGEX, "")
        .replace(SPAN_1_REGEX, "")
        .replace(SPAN_2_REGEX, "")
        .replace("\n<p>\n</p>", "")

    return source
        .splitToSequence("\n")
        .map { " * $it" }
        .joinToString(
            prefix = "/**\n",
            separator = "\n",
            postfix = "\n */"
        )
}
