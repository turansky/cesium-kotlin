package com.github.turansky.cesium

internal class Class(
    override val source: Definition
) : Declaration() {
    var companion: Namespace? = null

    override fun toCode(): String {
        val members = source.body
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
            .toList()

        var body = members
            .asSequence()
            .filter { !it.static }
            .map { it.toCode() }
            .filter { it.isNotEmpty() } // TEMP
            .joinToString(separator = "\n\n")

        val companionBody = members
            .asSequence()
            .filter { it.static }
            .map { it.toCode() }
            .filter { it.isNotEmpty() } // TEMP
            .joinToString(separator = "\n\n")

        if (companionBody.isNotEmpty()) {
            body += "\n\ncompanion object {\n$companionBody\n}"
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
