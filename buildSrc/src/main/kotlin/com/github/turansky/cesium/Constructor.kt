package com.github.turansky.cesium

internal const val CONSTRUCTOR_OPTIONS: String = "ConstructorOptions"

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

    fun toExtensionCode(): String {
        if (hiddenOptions) {
            if (parameters.size != 1)
                return ""

            val type = parent.name
            // language=Kotlin
            return """
                inline fun $type(
                    block: $type.() -> Unit
                ): $type =
                    $type().apply(block)
            """.trimIndent()
        }

        return ""
    }

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
                .filter { it.name == CONSTRUCTOR_OPTIONS }
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
