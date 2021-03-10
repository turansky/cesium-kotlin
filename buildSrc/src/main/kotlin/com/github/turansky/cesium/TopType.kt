package com.github.turansky.cesium

internal class TopType(
    override val source: Definition
) : Declaration() {
    override fun toCode(): String =
        DEFAULT_PACKAGE +
                source.doc +
                "\n\n" +
                typeDeclaration(source.body, true)

    companion object {
        const val PREFIX = "export type "
    }
}
