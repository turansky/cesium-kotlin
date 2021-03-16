package com.github.turansky.cesium

internal class Property(
    override val source: Definition
) : Member() {
    private val data = source.body.substringBefore(": ")
        .split(" ")

    override val name: String = data.last()
    private val modifiers = data.dropLast(1)
    override val static = "static" in modifiers
    val readOnly = "readonly" in modifiers

    val type = kotlinType(source.body.substringAfter(": "), name)

    override fun toCode(): String {
        return source.doc(DocLink(parent, this)) + "\n" +
                (if (abstract) "abstract " else "") +
                (if (readOnly) "val" else "var") +
                " $name: $type"
    }
}
