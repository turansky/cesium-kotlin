package com.github.turansky.cesium

internal class Type(
    override val source: Definition
) : Declaration() {
    companion object {
        const val PREFIX = "export type "
    }
}
