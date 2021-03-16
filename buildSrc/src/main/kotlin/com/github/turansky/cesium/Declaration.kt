package com.github.turansky.cesium

internal abstract class Declaration : HasName {
    protected abstract val source: Definition

    abstract fun toCode(): String
}
