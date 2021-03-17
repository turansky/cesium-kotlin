package com.github.turansky.cesium

internal class Property(
    override val source: Definition
) : Member() {
    private val data = source.body.substringBefore(": ")
        .split(" ")

    override val name: String = data.last()
    override val docName: String
        get() = if (static) ".$name" else name

    private val modifiers = data.dropLast(1)
    override val static = "static" in modifiers || (hasParent && parent is Namespace)
    val readOnly = "readonly" in modifiers

    val type = kotlinType(source.body.substringAfter(": "), name)

    override fun toCode(): String {
        return source.doc(DocLink(parent, this)) + "\n" +
                (if (abstract) "abstract " else "") +
                (if (overridden) "override " else "") +
                (if (readOnly) "val" else "var") +
                " $name: $type"
    }
}
