package com.github.turansky.cesium

internal class Function(
    override val source: Definition
) : Declaration() {
    override val name = source.parseFunctionName()
    private val parameters = source.parseFunctionParameters()
    private val returnType = source.parseFunctionReturnType()

    override fun toCode(): String {
        val returnExpression = returnType?.let { ": $it" } ?: ""
        val declaration = "external fun $name ${parameters.toCode()}$returnExpression"

        return if (source.doc.isNotEmpty()) {
            DEFAULT_PACKAGE +
                    source.doc +
                    "\n" +
                    declaration
        } else {
            declaration
        }
    }

    companion object {
        const val PREFIX = "export function "
    }
}
