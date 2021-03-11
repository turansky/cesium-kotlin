package com.github.turansky.cesium

internal class Constructor(
    override val source: Definition
) : Member() {
    override val static: Boolean = false

    private val parameters = source.body
        .splitToSequence(", ")
        .filter { it.isNotEmpty() }
        .map(::Parameter)
        .toList()

    override fun toCode(): String =
        when (parameters.size) {
            0 -> ""
            1 -> "(${parameters.single().toCode()})"
            else -> {
                val params = parameters
                    .joinToString(",\n") {
                        it.toCode()
                    }
                "(\n$params\n)"
            }
        }
}
