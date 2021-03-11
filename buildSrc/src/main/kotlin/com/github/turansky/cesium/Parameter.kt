package com.github.turansky.cesium

internal class Parameter(
    body: String
) {
    private val name = body.substringBefore(": ").removeSuffix("?")
    private val type = kotlinType(body.substringAfter(": "))
    private val optional = "?:" in body
    private val nullable = optional && type != "dynamic"

    var supportDefault: Boolean = true

    fun toCode(): String =
        " $name: $type" +
                (if (nullable) "?" else "") +
                (if (optional && supportDefault) " = definedExternally" else "")
}
