package com.github.turansky.cesium

private val OPTIONS_REGEX = Regex("""options: (\{.+})""", RegexOption.DOT_MATCHES_ALL)
private val INNER_OPTIONS_REGEX = Regex(""": \{.+}""", RegexOption.DOT_MATCHES_ALL)

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
            var constructorBody = body.removeSurrounding("constructor(", ")")
            val options = OPTIONS_REGEX.findAll(constructorBody)
                .map { it.groupValues[1] }
                .flatMap { source ->
                    val types = source.toOptionTypes("Constructor")
                    constructorBody = constructorBody.replaceFirst(source, types.first().name)
                    types.asSequence()
                }
                .toList()

            val constructor = Constructor(copy(body = constructorBody))
            sequenceOf(constructor) + options
        }

        body.startsWith("const ")
        -> sequenceOf(Constant(copy(body = body.removePrefix("const "))))

        body.isPropertyLike()
        -> sequenceOf(Property(this))

        else
        -> sequenceOf(Method(this))
    }

private fun String.isPropertyLike(): Boolean {
    val pi = indexOf(":")
    val mi = indexOf("(")
    return mi == -1 || pi < mi
}

private fun String.toOptionTypes(prefix: String): List<SimpleType> {
    // TODO: support inner options
    val body = replace(INNER_OPTIONS_REGEX, ": any")
    val type = SimpleType(Definition("", "${prefix}Options = $body"))
    return listOf(type)
}
