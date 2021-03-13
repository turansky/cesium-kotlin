package com.github.turansky.cesium

private const val DOC_ROOT = "https://cesium.com/docs/cesiumjs-ref-doc"
private const val GLOBAL_TEMPLATE = "$DOC_ROOT/global.html#{enum}"
private const val TOP_TEMPLATE = "$DOC_ROOT/{top}.html"
private const val MEMBER_TEMPLATE = "$DOC_ROOT/{type}.html#{member}"

internal class DocLink
private constructor(
    val href: String,
    val typeMode: Boolean = false
) {
    constructor(enum: IEnum)
            : this(GLOBAL_TEMPLATE.replace("{enum}", enum.name))

    constructor(top: ITop)
            : this(TOP_TEMPLATE.replace("{top}", top.name), top is IType)

    constructor(type: IType, member: IMember)
            : this(MEMBER_TEMPLATE.replace("{type}", type.name).replace("{member}", member.docName))
}
