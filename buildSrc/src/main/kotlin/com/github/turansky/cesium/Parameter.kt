package com.github.turansky.cesium

internal class Parameter(
    body: String
) {
    val name: String = body.substringBefore(": ").removeSuffix("?")
        .let { if (it == "object") "obj" else it }

    private val type: String by lazy {
        kotlinType(body.substringAfter(": "))
    }

    val optional: Boolean by lazy { "?:" in body.substringBefore("{") }
    private val nullable: Boolean = optional && type != "dynamic"

    var supportDefault: Boolean = true

    fun toCode(): String =
        " $name: $type" +
                (if (nullable) "?" else "") +
                (if (optional && supportDefault) " = definedExternally" else "")
}

internal fun List<Parameter>.toCode(): String =
    when (size) {
        0 -> "()"
        1 -> "(${single().toCode()})"
        else -> {
            val params = joinToString(",\n") {
                it.toCode()
            }

            "(\n$params\n)"
        }
    }
