package com.github.turansky.cesium

internal class Type(
    override val source: Definition
) : Declaration() {
    override fun toCode(): String =
        DEFAULT_PACKAGE +
                source.doc +
                "\n\n" +
                "typealias $fileName = Any"

    companion object {
        const val PREFIX = "export type "
    }
}
