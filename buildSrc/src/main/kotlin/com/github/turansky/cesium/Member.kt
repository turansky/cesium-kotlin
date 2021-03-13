package com.github.turansky.cesium

internal abstract class Member : Declaration(), IMember {
    lateinit var parent: IType
    abstract val static: Boolean
}
