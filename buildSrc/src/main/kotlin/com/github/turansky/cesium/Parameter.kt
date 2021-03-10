package com.github.turansky.cesium

internal class Parameter(
    body: String
) {
    private val name = body.substringBefore(": ").removeSuffix("?")
    private val type = kotlinType(body.substringAfter(": "))
    private val nullable = "?:" in body

    fun toCode(): String =
        " $name: $type" + if (nullable) "?" else ""
}
