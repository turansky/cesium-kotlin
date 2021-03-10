package com.github.turansky.cesium

internal class Namespace(
    override val source: Definition
) : Declaration() {
    val members: List<Member> = members(source.body)

    companion object {
        const val PREFIX = "export namespace "
    }
}
