package com.github.turansky.cesium

private val INTEGER_NAMES = setOf(
    "index",
    "key",
    "level",
    "length"
)

internal fun isInteger(name: String?): Boolean {
    name ?: return false

    return when {
        name in INTEGER_NAMES -> true

        name.endsWith("Index") -> true
        name.endsWith("Level") -> true
        name.endsWith("Length") -> true

        else -> false
    }
}
