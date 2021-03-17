package com.github.turansky.cesium

internal class Enum(
    override val source: Definition
) : Declaration(), IEnum {
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
            .joinToString(separator = "\n\n", postfix = "\n\n;\n") {
                it.toCode()
            }

        val type = if (LAZY_MODE) {
            "object /* enum */"
        } else {
            "enum class"
        }
        return DEFAULT_PACKAGE +
                source.doc(DocLink(this)) +
                "\n\n" +
                "external $type $name {\n\n$body\n}"
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
        val doc = source.doc()
        return if (doc.isNotBlank()) {
            "$doc\n${name},"
        } else {
            "$name,"
        }
    }
}
