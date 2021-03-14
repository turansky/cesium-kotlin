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
        .flatMap { parseTopDefinition(it) }
        .flatMap { it.toMembers() }
        .toList()
}

private fun Definition.toMembers(): Sequence<Member> =
    when {
        body.startsWith("namespace ") -> {
            val newBody = body
                .removePrefix("namespace ")
                .replace("\n    ", "\n")
                .removeSuffix("\n}")
            sequenceOf(NestedNamespace(copy(body = newBody)))
        }

        body.startsWith("type ")
        -> sequenceOf(SimpleType(copy(body = body.removePrefix("type "))))

        body.startsWith("constructor(")
        -> {
            val constructorBody = body.removeSurrounding("constructor(", ")")
            sequenceOf(Constructor(copy(body = constructorBody)))
        }

        body.startsWith("const ")
        -> sequenceOf(Constant(copy(body = body.removePrefix("const "))))

        else -> {
            val pi = body.indexOf(":")
            val mi = body.indexOf("(")
            if (mi == -1 || (pi < mi)) {
                sequenceOf(Property(this))
            } else {
                sequenceOf(Method(this))
            }
        }
    }
