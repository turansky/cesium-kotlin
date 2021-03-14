package com.github.turansky.cesium

internal class Constructor(
    override val source: Definition
) : Member() {
    override val name: String
        get() = TODO()

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
        if (!hiddenOptions) {
            val params = parameters.toCode()
            if (params.isNotEmpty()) {
                return "constructor$params"
            }
        }

        return ""
    }
}
