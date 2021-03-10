package com.github.turansky.cesium

internal class Function(
    override val source: Definition
) : Declaration() {
    override fun toCode(): String {
        return DEFAULT_PACKAGE +
                source.doc +
                "\n\n" +
                "external val $fileName: Function<dynamic>"
    }

    companion object {
        const val PREFIX = "export function "
    }
}
