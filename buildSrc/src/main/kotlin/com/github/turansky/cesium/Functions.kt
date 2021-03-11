package com.github.turansky.cesium

internal fun Definition.parseFunctionName(): String =
    body.substringBefore("(")
        .substringAfterLast(" ")

internal fun Definition.parseFunctionParameters(): List<Parameter> =
    body.substringAfter("(")
        .substringBeforeLast("): ")
        .splitToSequence(", ")
        .filter { it.isNotEmpty() }
        .map(::Parameter)
        .toList()

internal fun Definition.parseFunctionReturnType(): String? =
    body.substringAfterLast("): ")
        .let { kotlinType(it) }
        .takeIf { it != "Unit" }

