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
        hasHiddenOptions()
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

    private companion object {
        fun Constructor.hasHiddenOptions(): Boolean {
            val p = parameters.singleOrNull()
                ?: return false

            if (p.name != "options" || !p.optional)
                return false

            val klass = parent as Class

            val options = sequenceOf(klass, klass.companion)
                .filterNotNull()
                .flatMap { it.members.asSequence() }
                .filterIsInstance<SimpleType>()
                .filter { it.name == "ConstructorOptions" }
                .singleOrNull()
                ?: return false

            val mutablePropertyNames = klass.members
                .asSequence()
                .filterIsInstance<Property>()
                .filterNot { it.readOnly }
                .map { it.name }
                .toSet()

            return options.parameterNames
                .all { it in mutablePropertyNames }
        }
    }
}
