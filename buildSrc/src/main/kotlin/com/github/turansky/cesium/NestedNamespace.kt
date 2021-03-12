package com.github.turansky.cesium

internal class NestedNamespace(
    override val source: Definition
) : Member() {
    override val name: String =
        source.defaultName

    override val static: Boolean = true

    override fun toCode(): String =
        Namespace(source).toCode(false)
}
