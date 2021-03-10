package com.github.turansky.cesium

internal class Interface(
    source: Definition
) : TypeBase(source) {
    override val typeName: String = "interface"
    override var companion: HasMembers? = null

    companion object {
        const val PREFIX = "export interface "
    }
}
