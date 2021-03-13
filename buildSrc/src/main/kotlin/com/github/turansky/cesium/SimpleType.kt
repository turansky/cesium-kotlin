package com.github.turansky.cesium

internal class SimpleType(
    override val source: Definition
) : Member() {
    override val name: String =
        source.defaultName

    override val docName: String =
        ".$name"

    override val static: Boolean = false

    override fun toCode(): String =
        source.doc(DocLink(parent, this)) +
                "\n\n" +
                typeDeclaration(source.body, false)
}
