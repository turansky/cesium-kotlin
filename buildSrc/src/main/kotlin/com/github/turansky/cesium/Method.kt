package com.github.turansky.cesium

internal class Method(
    override val source: Definition
) : Member() {
    private val name = source.body
        .substringBefore("(")
        .substringAfterLast(" ")

    private val modifiers = source.body
        .substringBefore("(")
        .split(" ")
        .dropLast(1)

    override val static: Boolean = "static" in modifiers

    private val parameters = source.body
        .substringAfter("(")
        .substringBeforeLast("): ")
        .splitToSequence(", ")
        .filter { it.isNotEmpty() }
        .map(::Parameter)
        .toList()

    private val returnType: String = source.body
        .substringAfterLast("): ")
        .let { kotlinType(it) }

    override fun toCode(): String {
        if (name == "toString" && parameters.isEmpty())
            return ""

        val returnExpression = when (returnType) {
            "Unit" -> ""
            else -> ": $returnType"
        }

        return source.doc +
                "\n" +
                "fun $name ${parameters.toCode()}$returnExpression"
    }
}
