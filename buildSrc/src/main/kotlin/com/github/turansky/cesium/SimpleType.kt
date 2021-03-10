package com.github.turansky.cesium

internal open class SimpleType(
    override val source: Definition
) : Member() {
    override val static: Boolean
        get() = TODO("Do you call this for type?")

    override fun toCode(): String =
        source.doc +
                "\n\n" +
                typeDeclaration(source.body, false)
}
