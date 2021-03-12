package com.github.turansky.cesium

internal class Enum(
    override val source: Definition
) : Declaration() {
    override val name: String =
        source.defaultName

    override fun toCode(): String {
        val body = source.body
            .substringAfter("\n    ")
            .removeSuffix("}")
            .replace(",\n     *", "__COMMA__\n     *")
            .split(Regex(""",\n\s+"""))
            .asSequence()
            .map { it.replace("__COMMA__\n", ",\n") }
            .flatMap { parseTopDefinition(it) }
            .map { EnumConstant(it) }
            .joinToString(separator = ",\n\n", postfix = ",\n\n;\n") {
                it.toCode()
            }

        return DEFAULT_PACKAGE +
                source.doc +
                "\n\n" +
                "external enum class $name {\n\n$body\n}"
    }

    companion object {
        const val PREFIX = "export enum "
    }
}

// TODO: describe value in comments
internal class EnumConstant(
    override val source: Definition
) : Declaration() {
    override val name: String =
        source.body.split(" = ")[0]

    override fun toCode(): String {
        return if (source.doc.isNotBlank()) {
            "${source.doc}\n${name}"
        } else {
            name
        }
    }
}
