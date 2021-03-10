package com.github.turansky.cesium

internal class Namespace(
    source: Definition
) : TypeBase(source), HasMembers {
    override val typeName: String = "object"
    override val companion: HasMembers? = null

    override val members: List<Member> = members(source.body)

    companion object {
        const val PREFIX = "export namespace "
    }
}
