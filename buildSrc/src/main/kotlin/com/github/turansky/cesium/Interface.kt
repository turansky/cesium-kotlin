package com.github.turansky.cesium

internal class Interface(
    source: Definition
) : TypeBase(source) {
    override val typeName: String = "interface"
    override var companion: HasMembers? = null

    override fun suppresses(): List<String> {
        var result = super.suppresses()
        if (companion != null)
            result = result + "NESTED_CLASS_IN_EXTERNAL_INTERFACE"

        return result
    }

    companion object {
        const val PREFIX = "export interface "
    }
}
