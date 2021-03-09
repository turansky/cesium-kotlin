package com.github.turansky.cesium

internal class Interface(
    override val source: Definition
) : Declaration() {
    companion object {
        const val PREFIX = "export interface "
    }
}
