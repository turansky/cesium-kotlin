package com.github.turansky.cesium

private val INTEGER_NAMES = setOf(
    "index",
    "key",
    "level",
    "length",

    "year",
    "month",
    "day",
    "hour",
    "minute",
    "second",
    "millisecond",

    "indexOf"
)

internal fun isInteger(name: String?): Boolean {
    name ?: return false

    return when {
        name in INTEGER_NAMES -> true

        name == "focalLength" -> false
        name == "chordLength" -> false

        name.startsWith("numberOf") -> true

        name.endsWith("Index") -> true
        name.endsWith("Level") -> true
        name.endsWith("Length") -> true

        else -> false
    }
}
