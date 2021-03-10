package com.github.turansky.cesium

internal class Type(
    override val source: Definition
) : Declaration() {
    override fun toCode(): String =
        DEFAULT_PACKAGE +
                source.doc +
                "\n\n" +
                "typealias $fileName = ${getBody()}"

    private fun getBody(): String {
        val body = source.body.split(" = ")[1]
        if (!body.startsWith("("))
            return "Any"

        val (params, returnType) = body
            .removePrefix("(")
            .removeSuffix(";")
            .split(") => ")

        val parameters = params.splitToSequence(",")
            .filter { it.isNotEmpty() }
            .map(::Parameter)
            .map { it.toCode() }
            .joinToString(", ")

        return "($parameters) -> ${kotlinType(returnType)}"
    }

    companion object {
        const val PREFIX = "export type "
    }
}
