package com.github.turansky.cesium

private val STYLE_REGEX = Regex("""\n<style .+?\n</style>""", RegexOption.DOT_MATCHES_ALL)
private val DIV_REGEX = Regex("""\n<div .+?\n</div>""", RegexOption.DOT_MATCHES_ALL)
private val TABLE_REGEX = Regex("""\n\s*<table>.+?</table>""", RegexOption.DOT_MATCHES_ALL)
private val SPAN_1_REGEX = Regex("""\n<span .+?</span>""", RegexOption.DOT_MATCHES_ALL)
private val SPAN_2_REGEX = Regex("""<span .+?</span>\n""", RegexOption.DOT_MATCHES_ALL)
private val P_REGEX = Regex("""<p> ?(.+?)</p>""", RegexOption.DOT_MATCHES_ALL)
private val IMG_REGEX = Regex("""<img .+? />\n""")

private val PRE_CODE_REGEX = Regex("""<pre><code>(.+?)</code></pre>""", RegexOption.DOT_MATCHES_ALL)
private val PRE_REGEX = Regex("""<pre>(.+?)</pre>""", RegexOption.DOT_MATCHES_ALL)
private val CODE_REGEX = Regex("""<code>(.+?)</code>""")
private val CODE_MULTILINE_REGEX = Regex("""<code>(.+?)</code>""", RegexOption.DOT_MATCHES_ALL)

private val UL_REGEX = Regex("""<ul>(.+?)</ul>""", RegexOption.DOT_MATCHES_ALL)
private val LI_REGEX = Regex("""<li>(.+?)</li>""", RegexOption.DOT_MATCHES_ALL)

private val KDOC_KEYWORDS = setOf("@example", "@param", "@returns", "@property")
private val DELIMITER = "--DEL--"

internal fun kdoc(doc: String): String {
    if (doc.isEmpty())
        return ""

    val source = doc.removePrefix("/**\n")
        .substringBeforeLast("\n")
        .trimMargin("*")
        .splitToSequence("\n")
        .map { it.removePrefix(" ") }
        .joinToString("\n")
        .replace(STYLE_REGEX, "")
        .replace(DIV_REGEX, "")
        .replace("  The bit values are as follows:", "")
        .replace(TABLE_REGEX, "")
        .replace(SPAN_1_REGEX, "")
        .replace(SPAN_2_REGEX, "")
        .replace("\n<p>\n</p>", "")
        .replace("<br /><br />", "")
        .replace("<br />", "")
        .replace("<br>", "")
        .replace(P_REGEX, "$1")
        .replace(IMG_REGEX, "")
        .replace(PRE_CODE_REGEX, "```$1```")
        .replace(PRE_REGEX, "```$1```")
        .replace(CODE_REGEX, "`$1`")
        .replace(CODE_MULTILINE_REGEX, "```$1```")
        .replace("<p>\n", "")
        .replace(UL_REGEX) { listItems(it.groupValues[1]) }
        .replace("\n\n\n", "\n\n")
        .trim()
        .let(::formatBlocks)

    return source
        .splitToSequence("\n")
        .map { " * $it" }
        .joinToString(
            prefix = "/**\n",
            separator = "\n",
            postfix = "\n */"
        )
}

private fun listItems(source: String): String =
    LI_REGEX.findAll(source)
        .map { it.groupValues[1] }
        .map { it.trim() }
        .map { "- $it" }
        .joinToString("\n")

private fun formatBlocks(source: String): String =
    KDOC_KEYWORDS.asSequence()
        .fold(source) { acc, keyword ->
            acc.replace("$keyword ", "$DELIMITER$keyword ")
        }
        .splitToSequence(DELIMITER)
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .map { formatBlock(it) }
        .joinToString("\n")

private fun formatBlock(source: String): String =
    when {
        source.startsWith("@returns") -> source.replace("@returns", "@return")
        else -> source
    }
