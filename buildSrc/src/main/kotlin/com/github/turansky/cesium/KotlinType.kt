package com.github.turansky.cesium

private val CLASS_REGEX = Regex("""[\w\d]+""")

private val STANDARD_TYPE_MAP = mapOf(
    "any" to "Any",
    "object" to "Any",

    "boolean" to "Boolean",
    "number" to "Double",
    "string" to "String",

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
    // TODO: use interface
    if (type == "InterpolationAlgorithm" || type == "Packable")
        return "dynamic"

    if (STANDARD_TYPE_MAP.containsKey(type))
        return STANDARD_TYPE_MAP.getValue(type)

    if (CLASS_REGEX.matches(type) && type.get(0) == type.get(0).toUpperCase())
        return type

    if (type.endsWith(" | undefined") && type.indexOf("|") == type.lastIndexOf("|"))
        return kotlinType(type.removeSuffix(" | undefined")) + "?"

    if (type.endsWith("[]") && "|" !in type)
        return "Array<out ${kotlinType(type.removeSuffix("[]"))}>"

    val promiseResult = type.removeSurrounding("Promise<", ">")
    if (promiseResult != type)
        return "kotlin.js.Promise<${kotlinType(promiseResult)}>"

    // TODO: add aliases support

    return "dynamic"
}
