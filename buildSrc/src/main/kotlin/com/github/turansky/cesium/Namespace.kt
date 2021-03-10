package com.github.turansky.cesium

internal class Namespace(
    override val source: Definition
) : Declaration() {
    val members: List<Member> = members(source.body)

    override fun toCode(): String {
        val body = members
            .asSequence()
            .map { it.toCode() }
            .filter { it.isNotEmpty() } // TEMP
            .joinToString(separator = "\n\n")

        return DEFAULT_PACKAGE +
                source.doc +
                "\n\n" +
                "external object $fileName {\n$body\n}"
    }

    companion object {
        const val PREFIX = "export namespace "
    }
}
