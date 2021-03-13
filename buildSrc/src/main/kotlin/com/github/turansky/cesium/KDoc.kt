package com.github.turansky.cesium

private val DIV_REGEX = Regex("""\n<div .+?\n</div>""", RegexOption.DOT_MATCHES_ALL)
private val SPAN_1_REGEX = Regex("""\n<span .+?</span>""", RegexOption.DOT_MATCHES_ALL)
private val SPAN_2_REGEX = Regex("""<span .+?</span>\n""", RegexOption.DOT_MATCHES_ALL)
private val P_REGEX = Regex("""<p> ?(.+?)</p>""", RegexOption.DOT_MATCHES_ALL)
private val PRE_CODE_REGEX = Regex("""<pre><code>(.+?)</code></pre>""", RegexOption.DOT_MATCHES_ALL)
private val PRE_REGEX = Regex("""<pre>(.+?)</pre>""", RegexOption.DOT_MATCHES_ALL)
private val CODE_REGEX = Regex("""<code>(.+?)</code>""")

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
        .replace("<br /><br />", "")
        .replace("<br />", "")
        .replace(P_REGEX, "$1")
        .replace(PRE_CODE_REGEX, "```$1```")
        .replace(PRE_REGEX, "```$1```")
        .replace(CODE_REGEX, "`$1`")
        .replace("\n\n\n", "\n\n")
        .trim()

    return source
        .splitToSequence("\n")
        .map { " * $it" }
        .joinToString(
            prefix = "/**\n",
            separator = "\n",
            postfix = "\n */"
        )
}
