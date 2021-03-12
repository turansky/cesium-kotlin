package com.github.turansky.cesium

internal class Method(
    override val source: Definition
) : Member() {
    private val name = source.parseFunctionName()

    private val modifiers = source.body
        .substringBefore("(")
        .split(" ")
        .dropLast(1)

    override val static: Boolean = "static" in modifiers

    private val parameters = source.parseFunctionParameters()
    private val returnType = source.parseFunctionReturnType()

    override fun toCode(): String {
        if (name == "toString" && parameters.isEmpty())
            return ""

        val returnExpression = returnType?.let { ": $it" } ?: ""

        val doc = source.doc
            .let { if (it.isNotEmpty()) "$it\n" else "" }

        return "$doc fun $name ${parameters.toCode()}$returnExpression"
    }
}
