package com.github.turansky.cesium

const val PACKABLE: String = "Packable"

fun applyPackableFixes(code: String): String =
    code.replace(": Any", ": T")
        .replace("interface $PACKABLE", "interface $PACKABLE<T: Any>")
        .replace("companion object {", "")
        .replace("}\n}\n", "}\n")
