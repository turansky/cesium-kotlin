package com.github.turansky.cesium

internal abstract class Declaration {
    abstract val name: String
    protected abstract val source: Definition

    abstract fun toCode(): String
}
