package com.github.turansky.cesium

internal class Parameter(
    body: String
) {
    val name: String = body.substringBefore(": ")
        .removePrefix("...")
        .removeSuffix("?")
        .let { if (it == "object") "obj" else it }

    private val type: String by lazy {
        kotlinType(body.substringAfter(": "))
    }

    private val vararg: Boolean by lazy { "..." in body.substringBefore("{") }

    val optional: Boolean by lazy { "?:" in body.substringBefore("{") }
    private val nullable: Boolean = optional && !type.startsWith("dynamic")

    var supportDefault: Boolean = true

    fun toCode(): String =
        (if (vararg) "vararg " else "") +
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
