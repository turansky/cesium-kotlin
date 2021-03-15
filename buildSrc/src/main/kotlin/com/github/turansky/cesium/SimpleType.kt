package com.github.turansky.cesium

internal class SimpleType(
    override val source: Definition,
    override val static: Boolean = false
) : Member() {
    override val name: String =
        source.defaultName

    override val docName: String =
        ".$name"

    val parameterNames: List<String> by lazy {
        source.body
            .substringAfter("\n")
            .substringBeforeLast("\n")
            .trimIndent()
            .splitToSequence("\n")
            .filter { !it.startsWith(" ") }
            .filter { !it.startsWith("}") }
            .map { it.substringBefore(": ") }
            .map { it.removeSuffix("?") }
            .toList()
    }

    override fun toCode(): String =
        source.doc(DocLink(parent, this))
            .let { if (it.isNotEmpty()) "$it\n" else "" } +
                typeDeclaration(source.body, false)

    override fun equals(other: Any?): Boolean =
        other is SimpleType && source == other.source

    override fun hashCode(): Int =
        source.hashCode()
}
