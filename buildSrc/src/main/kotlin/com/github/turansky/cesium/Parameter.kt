package com.github.turansky.cesium

internal class Parameter(
    body: String
) {
    private val name = body.substringBefore(": ").removeSuffix("?")
    private val type = kotlinType(body.substringAfter(": "))
    private val optional = "?:" in body

    var supportDefault: Boolean = true

    fun toCode(): String =
        " $name: $type" +
                (if (optional) "?" else "") +
                (if (supportDefault) " = definedExternally" else "")
}
