package com.github.turansky.cesium

internal fun members(
    body: String
): List<Member> =
    body
        .substringAfter("\n    ")
        .removeSuffix(";\n}")
        .splitToSequence(";\n    /")
        .map { if (it.startsWith("**")) "/$it" else it }
        .map { parseTopDefinition(it) }
        .map { it.toMember() }
        .toList()

private fun Definition.toMember(): Member =
    when {
        body.startsWith("type ")
        -> SimpleType(copy(body = body.removePrefix("type ")))

        body.startsWith("constructor(")
        -> Constructor(this)

        body.startsWith("const ")
        -> Constant(copy(body = body.removePrefix("const ")))

        "(" !in body
        -> Property(this)

        else
        -> Method(this)
    }
