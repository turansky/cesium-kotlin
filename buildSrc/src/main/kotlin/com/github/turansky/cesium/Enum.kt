package com.github.turansky.cesium

internal class Enum(
    override val source: Definition
) : Declaration() {
    companion object {
        const val PREFIX = "export enum "
    }
}
