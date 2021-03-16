package com.github.turansky.cesium

internal class Method(
    override val source: Definition
) : Member() {
    override val name = source.parseFunctionName()

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

        if (name == "equals" && parameters.size == 1)
            return ""

        val returnExpression = returnType?.let { ": $it" } ?: ""

        val modifier = if (hasParent) "" else "external "
        val link = if (hasParent) {
            DocLink(parent, this)
        } else {
            DocLink(this)
        }

        val doc = source.doc(link)
            .let { if (it.isNotEmpty()) "$it\n" else "" }

        return doc +
                "$modifier fun $name ${parameters.toCode()}$returnExpression"
    }
}
