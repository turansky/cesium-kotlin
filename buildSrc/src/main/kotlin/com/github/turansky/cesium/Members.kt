package com.github.turansky.cesium

internal fun members(
    body: String
): List<Member> {
    if (body.endsWith("{\n}"))
        return emptyList()

    return body
        .substringAfter("\n    ")
        .removeSuffix(";\n}")
        .splitToSequence(";\n    /")
        .map { if (it.startsWith("**")) "/$it" else it }
        .map { parseTopDefinition(it) }
        .map { it.toMember() }
        .toList()
}

private fun Definition.toMember(): Member =
    when {
        body.startsWith("namespace ") -> {
            val newBody = body
                .removePrefix("namespace ")
                .replace("\n    ", "\n")
                .removeSuffix("\n}")
            NestedNamespace(copy(body = newBody))
        }

        body.startsWith("type ")
        -> SimpleType(copy(body = body.removePrefix("type ")))

        body.startsWith("constructor(")
        -> Constructor(copy(body = body.removeSurrounding("constructor(", ")")))

        body.startsWith("const ")
        -> Constant(copy(body = body.removePrefix("const ")))

        "(" !in body
        -> Property(this)

        else
        -> Method(this)
    }
