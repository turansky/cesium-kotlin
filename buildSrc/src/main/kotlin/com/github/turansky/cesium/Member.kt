package com.github.turansky.cesium

internal abstract class Member : Declaration(), IMember {
    lateinit var parent: IType
    protected val hasParent: Boolean
        get() = ::parent.isInitialized

    abstract val static: Boolean
}
