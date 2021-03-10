package com.github.turansky.cesium

internal class Class(
    source: Definition
) : TypeBase(source) {
    override val typeName: String = "class"
    override var companion: HasMembers? = null
    override val staticBody: Boolean = true

    companion object {
        const val PREFIX = "export class "
    }
}
