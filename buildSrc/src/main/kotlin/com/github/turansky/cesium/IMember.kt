package com.github.turansky.cesium

internal interface IMember {
    val name: String

    val docName: String
        get() = name
}
