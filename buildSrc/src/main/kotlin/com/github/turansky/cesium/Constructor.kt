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

    val hiddenOptions: Boolean by lazy {
        val p = parameters.singleOrNull()
        p != null && p.name == "options" && p.optional
    }

    override fun toCode(): String {
        if (hiddenOptions)
            return ""

        return when (parameters.size) {
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
}
