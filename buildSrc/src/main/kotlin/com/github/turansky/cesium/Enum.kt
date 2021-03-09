package com.github.turansky.cesium

internal class Enum(
    override val source: Definition
) : Declaration() {
    override fun toCodeBody(): String {
        val body = source.body
            .substringAfter("\n")
            .removeSuffix("}")
            .split(Regex(""" = \d+,\n\s+"""))
            .map { parseTopDefinition(it) }
            .joinToString(separator = ",\n\n", postfix = ",\n\n;\n") {
                "${it.doc}\n${it.body}"
            }

        return "enum class $name {\n\n$body\n}"
    }

    companion object {
        const val PREFIX = "export enum "
    }
}
