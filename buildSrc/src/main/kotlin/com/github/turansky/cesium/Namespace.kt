package com.github.turansky.cesium

internal class Namespace(
    source: Definition
) : TypeBase(source) {
    override val typeName: String = "object"
    override val companion: HasMembers? = null

    companion object {
        const val PREFIX = "export namespace "
    }
}
