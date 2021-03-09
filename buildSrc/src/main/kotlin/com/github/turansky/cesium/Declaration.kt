package com.github.turansky.cesium

internal abstract class Declaration {
    protected abstract val source: Definition

    val fileName: String by lazy {
        source.body.substringBefore(" ")
    }

    open fun toCode(): String =
        source.doc + "\n\n" + source.body
}
