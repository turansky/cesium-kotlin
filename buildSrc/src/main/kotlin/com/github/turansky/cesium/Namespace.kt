package com.github.turansky.cesium

internal class Namespace(
    source: Definition
) : TypeBase(source) {
    override val typeName: String = "object"
    override val companion: HasMembers? = null
    override val staticBody: Boolean = true

    companion object {
        const val PREFIX = "export namespace "
    }
}
