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

private val UL_REGEX = Regex(""" *<ul>(.+?)</ul>""", RegexOption.DOT_MATCHES_ALL)
private val LI_REGEX = Regex("""<li>(.+?)</li>""", RegexOption.DOT_MATCHES_ALL)

private val LINK_TYPE_REGEX = Regex("""\{@link ([\w\d]+)}""")
private val LINK_MEMBER_REGEX = Regex("""\{@link ([\w\d]+)[#.]([\w\d]+)}""")
private val LINK_HTTP_NAMED_1_REGEX = Regex("""\{@link (http.+?)\|(.+?)}""")
private val LINK_HTTP_NAMED_2_REGEX = Regex("""\[(.+?)]\{@link (http.+?)}""")
private val LINK_HTTP_REGEX = Regex("""\{@link (http.+?)}""")

private val KDOC_KEYWORDS = setOf("@example", "@param", "@returns", "@property")
private const val DELIMITER = "--DEL--"

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
        .replace(LINK_TYPE_REGEX, "[$1]")
        .replace(LINK_MEMBER_REGEX, "[$1.$2]")
        .replace(LINK_HTTP_NAMED_1_REGEX, "[$2]($1)")
        .replace(LINK_HTTP_NAMED_2_REGEX, "[$1]($2)")
        .replace(LINK_HTTP_REGEX, "[$1]")
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
        .map { it.multiline() }
        .joinToString("\n")

private fun formatBlocks(source: String): String =
    KDOC_KEYWORDS
        .fold(source) { acc, keyword ->
            acc.replace("$keyword ", "$DELIMITER$keyword ")
                .replace("$keyword\n", "$DELIMITER$keyword\n")
        }
        .splitToSequence(DELIMITER)
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .map { formatBlock(it) }
        .joinToString("\n")

private fun formatBlock(source: String): String =
    when {
        source.startsWith("@example") -> source.removePrefix("@example\n").let { "```\n$it\n```" }
        source.startsWith("@param") -> formatParam(source)
        source.startsWith("@returns") -> source.replace("@returns", "@return").multiline()
        else -> source
    }

private fun formatParam(source: String): String {
    val body = source.removePrefix("@param ")

    val name: String
    val default: String?
    val description: String

    if (body.startsWith("[")) {
        val data = body.removePrefix("[")
            .substringBefore("] ")
            .split(" = ")
        name = data.first()
        default = data.getOrNull(1)
        description = body.substringAfter("] ")
    } else {
        name = body.substringBefore(" ")
        default = null
        description = body.substringAfter(" ")
    }

    val defaultLines = default?.let { arrayOf("Default value - `$it`") }
        ?: emptyArray()

    val desc = description.removePrefix("- ")
        .multiline(*defaultLines)

    return "@param [$name] $desc"
}

private fun String.multiline(
    vararg additionalLines: String
): String =
    splitToSequence("\n")
        .plus(additionalLines)
        .map { it.trim() }
        .mapIndexed { index, line -> if (index == 0) line else "  $line" }
        .joinToString("\n")

