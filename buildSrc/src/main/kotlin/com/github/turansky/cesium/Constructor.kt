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

    override fun toCode(): String =
        parameters
            .dropLast(if (hiddenOptions) 1 else 0)
            .toCode()
            .takeIf { it.isNotEmpty() }
            ?.let { "constructor$it" }
            ?: ""

    private companion object {
        fun Constructor.hasHiddenOptions(): Boolean {
            parameters.lastOrNull()
                ?.takeIf { it.name == "options" }
                ?.takeIf { it.optional }
                ?: return false

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
