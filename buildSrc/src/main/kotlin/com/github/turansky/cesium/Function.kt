package com.github.turansky.cesium

internal class Function(
    override val source: Definition
) : Declaration() {
    private val name = source.parseFunctionName()
    private val parameters = source.parseFunctionParameters()
    private val returnType = source.parseFunctionReturnType()

    override fun toCode(): String {
        val returnExpression = returnType?.let { ": $it" } ?: ""

        return DEFAULT_PACKAGE +
                source.doc +
                "\n" +
                "external fun $name ${parameters.toCode()}$returnExpression"
    }

    companion object {
        const val PREFIX = "export function "
    }
}
