package com.github.turansky.cesium

internal abstract class TypeBase(
    override val source: Definition
) : Declaration() {
    abstract val typeName: String
    abstract val companion: HasMembers?
    open val staticBody: Boolean = false

    override fun toCode(): String {
        val members = members(source.body)
        val companionMembers = companion?.members ?: emptyList()

        var body = members
            .asSequence()
            .filter { staticBody || !it.static }
            .map { it.toCode() }
            .filter { it.isNotEmpty() } // TEMP
            .joinToString(separator = "\n\n")

        if (!staticBody) {
            val companionBody = members
                .asSequence()
                .filter { it.static }
                .plus(companionMembers)
                .map { it.toCode() }
                .filter { it.isNotEmpty() } // TEMP
                .joinToString(separator = "\n\n")

            if (companionBody.isNotEmpty()) {
                body += "\n\ncompanion object {\n$companionBody\n}"
            }
        }

        return DEFAULT_PACKAGE +
                source.doc +
                "\n\n" +
                "external $typeName $fileName {\n$body\n}"
    }
}
