package com.github.turansky.cesium

internal class Property(
    override val source: Definition
) : Declaration() {
    private val data = source.body.substringBefore(": ")
        .split(" ")

    private val name: String = data.last()
    private val modifiers = data.dropLast(1)
    val static = "static" in modifiers
    val readOnly = "readonly" in modifiers

    override fun toCode(): String {
        return source.doc + "\n" +
                (if (readOnly) "val" else "var") +
                " $name: dynamic"
    }
}
