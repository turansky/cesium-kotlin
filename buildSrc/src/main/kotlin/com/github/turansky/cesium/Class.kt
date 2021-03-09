package com.github.turansky.cesium

internal class Class(
    override val source: Definition
) : Declaration() {
    override fun toCode(): String {
        val body = source.body
            .substringAfter("\n    ")
            .removeSuffix(";\n}")
            .splitToSequence(";\n    /")
            .map { if (it.startsWith("**")) "/$it" else it }
            .map { parseTopDefinition(it) }
            .map {
                val body = it.body
                if (body.startsWith("constructor")) {
                    Constructor(it)
                } else {
                    if ("(" !in body) {
                        Property(it)
                    } else {
                        Method(it)
                    }
                }
            }
            .joinToString(separator = "\n\n") {
                it.toCode()
            }

        return DEFAULT_PACKAGE +
                source.doc +
                "\n\n" +
                "external class $fileName {\n$body\n}"
    }

    companion object {
        const val PREFIX = "export class "
    }
}
