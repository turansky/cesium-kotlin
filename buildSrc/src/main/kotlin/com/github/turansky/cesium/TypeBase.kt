package com.github.turansky.cesium

internal abstract class TypeBase(
    final override val source: Definition
) : Declaration(), HasMembers {
    abstract val typeName: String
    abstract val companion: HasMembers?
    open val staticBody: Boolean = false

    override val members = members(source.body)

    open fun suppresses(): List<String> {
        val hasAliases = sequenceOf(this, companion)
            .filterNotNull()
            .flatMap { it.members.asSequence() }
            .any { it is SimpleType }

        return if (hasAliases) {
            listOf("TOPLEVEL_TYPEALIASES_ONLY")
        } else emptyList()
    }

    override fun toCode(): String {
        val companionMembers = companion?.members ?: emptyList()

        var body = members
            .asSequence()
            .filter { staticBody || !it.static }
            .map { it.toCode() }
            .filter { it.isNotEmpty() } // TEMP
            .joinToString(separator = "\n\n")

        val suppresses = suppresses()
        val suppressHeader = if (suppresses.isNotEmpty()) {
            suppresses.asSequence()
                .map { """"$it"""" }
                .joinToString(", ")
                .let { "@file:Suppress($it)" }
        } else ""

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

        return suppressHeader +
                DEFAULT_PACKAGE +
                source.doc +
                "\n\n" +
                "external $typeName $fileName {\n$body\n}"
    }
}
