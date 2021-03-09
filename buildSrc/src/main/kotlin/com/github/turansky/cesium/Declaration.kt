package com.github.turansky.cesium

internal abstract class Declaration {
    protected abstract val source: Definition

    val name: String by lazy {
        source.body.substringBefore(" ")
    }

    fun toCode(): String =
        "package cesium\n\n" +
                source.doc + "\n\n" + toCodeBody()

    open fun toCodeBody(): String =
        source.body
}
