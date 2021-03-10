package com.github.turansky.cesium

internal fun typeDeclaration(
    source: String,
    top: Boolean
): String {
    val (name, body) = source.split(" = ")
    return if (body.startsWith("(")) {
        val suppress = if (!top) {
            """@Suppress("TOPLEVEL_TYPEALIASES_ONLY")""" + "\n"
        } else ""

        suppress + "typealias $name = ${typeBody(body)}"
    } else {
        (if (top) "external " else "") + "interface $name {}"
    }
}

private fun typeBody(body: String): String {
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
