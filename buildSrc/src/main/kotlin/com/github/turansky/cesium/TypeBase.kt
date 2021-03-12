package com.github.turansky.cesium

import com.github.turansky.cesium.Suppress.TOPLEVEL_TYPEALIASES_ONLY

internal abstract class TypeBase(
    final override val source: Definition
) : Declaration(), HasMembers {
    abstract val typeName: String
    abstract val companion: HasMembers?
    open val staticBody: Boolean = false

    override val members by lazy {
        members(source.body)
    }

    open fun suppresses(): List<Suppress> {
        val hasAliases = sequenceOf(this, companion)
            .filterNotNull()
            .flatMap { it.members.asSequence() }
            .any { it is SimpleType }

        return if (hasAliases) {
            listOf(TOPLEVEL_TYPEALIASES_ONLY)
        } else emptyList()
    }

    override fun toCode(): String =
        toCode(true)

    fun toCode(top: Boolean): String {
        val constructor = members.firstOrNull() as? Constructor
        val constructorBody = constructor?.toCode()
            ?.removePrefix("constructor")
            ?: ""

        val companionMembers = companion?.members
            ?.filterNot { it.isNestedType() }
            ?: emptyList()

        val nestedTypes = companion?.members
            ?.filter { it.isNestedType() }
            ?.filter(constructor.toMemberFilter())
            ?: emptyList()

        var body = members
            .asSequence()
            .filter { it != constructor }
            .filter { staticBody || !it.static }
            .plus(nestedTypes)
            .map { it.toCode() }
            .filter { it.isNotEmpty() } // TEMP
            .joinToString(separator = "\n\n")

        val suppresses = suppresses()
        val suppressHeader = if (suppresses.isNotEmpty()) {
            suppresses.asSequence()
                .map { """"${it.name}"""" }
                .joinToString(", ")
                .let { "@file:Suppress($it)\n\n" }
        } else ""

        if (!staticBody) {
            val companionBody = members
                .asSequence()
                .filter { it.static }
                .plus(companionMembers)
                .map { it.toCode() }
                .joinToString(separator = "\n\n")

            if (companionBody.isNotEmpty()) {
                body += "\n\ncompanion object {\n$companionBody\n}"
            }
        }

        // TODO: move cleanup to separate method
        body = "$constructorBody {\n$body\n}"
            .replace(": $fileName.", ": ")

        val header = if (top) {
            suppressHeader +
                    DEFAULT_PACKAGE
        } else ""

        val modifier = if (top) "external" else ""
        return header +
                source.doc +
                "\n" +
                "$modifier $typeName $fileName $body"
    }
}

private fun Member.isNestedType(): Boolean =
    this is SimpleType || this is NestedNamespace

private fun Constructor?.toMemberFilter(): (Member) -> Boolean {
    if (this == null || !hiddenOptions)
        return { true }

    return {
        it !is SimpleType || it.fileName != "ConstructorOptions"
    }
}
