package com.github.turansky.cesium

internal class Constructor(
    override val source: Definition
) : Member() {
    override val static: Boolean = false

    override fun toCode(): String {
        return ""
    }
}
