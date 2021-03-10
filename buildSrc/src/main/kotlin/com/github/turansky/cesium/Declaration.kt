package com.github.turansky.cesium

internal abstract class Declaration {
    protected abstract val source: Definition

    // use minBy instead
    val fileName: String by lazy {
        sequenceOf(
            source.body.substringBefore(" "),
            source.body.substringBefore("(")
        ).sortedBy { it.length }.first()
    }

    open fun toCode(): String =
        source.doc + "\n\n" + source.body
}
