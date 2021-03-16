package com.github.turansky.cesium

internal interface HasName {
    val name: String

    val docName: String
        get() = name
}

internal interface ITop : HasName

internal interface IEnum : HasName

internal interface IType : ITop

internal interface IMember : HasName
