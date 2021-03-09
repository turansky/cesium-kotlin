package com.github.turansky.cesium

internal class Namespace(
    override val source: Definition
) : Declaration() {
    companion object {
        const val PREFIX = "export namespace "
    }
}
