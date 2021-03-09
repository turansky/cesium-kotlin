package com.github.turansky.cesium

internal abstract class Declaration {
    protected abstract val source: Definition

    val name: String by lazy {
        source.body.substringBefore(" ")
    }

    fun toCode(): String =
        source.doc + "\n\n" + source.body
}
