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
        -> SimpleType(this)

        body.startsWith("constructor(")
        -> Constructor(this)

        "(" !in body
        -> Property(this)

        else
        -> Method(this)
    }
