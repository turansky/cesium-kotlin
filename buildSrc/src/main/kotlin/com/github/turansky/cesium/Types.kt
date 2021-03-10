package com.github.turansky.cesium

internal fun typeDeclaration(
    body: String
): String {
    val (name, source) = body.split(" = ")
    return "typealias $name = ${typeBody(source)}"
}

private fun typeBody(body: String): String {
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
