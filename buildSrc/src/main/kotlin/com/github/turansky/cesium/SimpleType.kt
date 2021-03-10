package com.github.turansky.cesium

internal open class SimpleType(
    override val source: Definition
) : Member() {
    override val static: Boolean = false

    override fun toCode(): String =
        source.doc +
                "\n\n" +
                typeDeclaration(source.body, false)
}
