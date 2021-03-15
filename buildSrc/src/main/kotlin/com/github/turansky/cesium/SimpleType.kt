package com.github.turansky.cesium

internal class SimpleType(
    override val source: Definition
) : Member() {
    override val name: String =
        source.defaultName

    override val docName: String =
        ".$name"

    override val static: Boolean = false

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
        source.doc(DocLink(parent, this)) +
                "\n" +
                typeDeclaration(source.body, false)
}
