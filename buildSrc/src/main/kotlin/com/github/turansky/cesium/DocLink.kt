package com.github.turansky.cesium

private const val DOC_ROOT = "https://cesium.com/docs/cesiumjs-ref-doc"
private const val TOP_TEMPLATE = "$DOC_ROOT/{top}.html"
private const val MEMBER_TEMPLATE = "$DOC_ROOT/{type}.html#{member}"

internal class DocLink
private constructor(
    val href: String
) {
    constructor(top: ITop)
            : this(TOP_TEMPLATE.replace("{top}", top.name))

    constructor(type: IType, member: IMember)
            : this(MEMBER_TEMPLATE.replace("{type}", type.name).replace("{member}", member.name))
}
