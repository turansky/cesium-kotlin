package com.github.turansky.cesium

internal class Class(
    override val source: Definition
) : Declaration() {
    companion object {
        const val PREFIX = "export class "
    }
}
