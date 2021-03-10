package com.github.turansky.cesium

private val CLASS_REGEX = Regex("""[\w\d]+""")

private val STANDARD_TYPE_MAP = mapOf(
    "any" to "Any",
    "object" to "Any",

    "boolean" to "Boolean",
    "number" to "Double",
    "string" to "String",

    "void" to "Unit",

    "Element" to "org.w3c.dom.Element",
    "HTMLElement" to "org.w3c.dom.HTMLElement",
    "HTMLImageElement" to "org.w3c.dom.HTMLImageElement",
    "HTMLCanvasElement" to "org.w3c.dom.HTMLCanvasElement",
    "HTMLVideoElement" to "org.w3c.dom.HTMLVideoElement",
    "HTMLIFrameElement" to "org.w3c.dom.HTMLIFrameElement",

    "ArrayBuffer" to "org.khronos.webgl.ArrayBuffer",
    "Uint8Array" to "org.khronos.webgl.Uint8Array",
    "Float32Array" to "org.khronos.webgl.Float32Array",

    "CameraEventType | any[] | undefined" to "CameraEventType?",
    "any[] | GeometryInstance" to "GeometryInstance",
    "GeometryInstance[] | GeometryInstance" to "Array<out GeometryInstance>"
)

internal fun kotlinType(
    type: String
): String {
    if (STANDARD_TYPE_MAP.containsKey(type))
        return STANDARD_TYPE_MAP.getValue(type)

    if (type.isClassLike())
        return type

    if (type.endsWith(" | undefined") && type.indexOf("|") == type.lastIndexOf("|"))
        return kotlinType(type.removeSuffix(" | undefined")) + "?"

    if (type.endsWith("[]") && "|" !in type)
        return "Array<out ${kotlinType(type.removeSuffix("[]"))}>"

    val promiseResult = type.removeSurrounding("Promise<", ">")
    if (promiseResult != type)
        return "kotlin.js.Promise<${kotlinType(promiseResult)}>"

    return "dynamic"
}

private fun String.isClassLike(): Boolean =
    if ("." in this) {
        val types = split(".")
        types.size == 2 && types.all { it.isClassLike() }
    } else {
        CLASS_REGEX.matches(this) && get(0) == get(0).toUpperCase()
    }
