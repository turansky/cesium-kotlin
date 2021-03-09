package com.github.turansky.cesium

private val CLASS_REGEX = Regex("""[\w\d]+""")

private val STANDARD_TYPE_MAP = mapOf(
    "any" to "Any",

    "boolean" to "Boolean",
    "number" to "Double",
    "string" to "String"
)

internal fun kotlinType(
    type: String
): String {
    if (STANDARD_TYPE_MAP.containsKey(type))
        return STANDARD_TYPE_MAP.getValue(type)

    if (CLASS_REGEX.matches(type))
        return type

    return "dynamic"
}
