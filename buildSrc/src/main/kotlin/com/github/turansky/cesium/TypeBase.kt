package com.github.turansky.cesium

import com.github.turansky.cesium.Suppress.TOPLEVEL_TYPEALIASES_ONLY

internal abstract class TypeBase(
    final override val source: Definition
) : Declaration(), IType, HasMembers {
    override val name: String =
        source.defaultName

    abstract val typeName: String
    abstract val companion: HasMembers?
    open val staticBody: Boolean = false

    override val members by lazy {
        members(source.body)
            .onEach { it.parent = this }
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
            ?: emptyList()

        var body = members
            .asSequence()
            .filter { it != constructor }
            .filter { staticBody || !it.static }
            .plus(nestedTypes)
            // WA for duplicated option types
            .distinct()
            .filter(constructor.toMemberFilter())
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
            .replace(": $name.", ": ")

        val header = if (top) {
            suppressHeader +
                    DEFAULT_PACKAGE
        } else ""

        val modifier = if (top) "external" else ""
        return header +
                source.doc(DocLink(this)) +
                "\n" +
                "$modifier $typeName $name $body"
    }
}

private fun Member.isNestedType(): Boolean {
    if (this is NestedNamespace)
        return true

    if (this !is SimpleType)
        return false

    return name.startsWith("ConstructorOptions")
            || !name.endsWith("Options")
}

private fun Constructor?.toMemberFilter(): (Member) -> Boolean {
    if (this == null || !hiddenOptions)
        return { true }

    return {
        it !is SimpleType || !it.name.startsWith("ConstructorOptions")
    }
}
