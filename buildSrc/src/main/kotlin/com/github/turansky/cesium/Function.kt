package com.github.turansky.cesium

internal class Function(
    override val source: Definition
) : Declaration() {
    companion object {
        const val PREFIX = "export function "
    }
}
