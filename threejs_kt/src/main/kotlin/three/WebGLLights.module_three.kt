@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import org.khronos.webgl.WebGLRenderingContext

external interface `T$54` {
    var directionalLength: Number
    var pointLength: Number
    var spotLength: Number
    var rectAreaLength: Number
    var hemiLength: Number
    var numDirectionalShadows: Number
    var numPointShadows: Number
    var numSpotShadows: Number
}

external interface `T$55` {
    var version: Number
    var hash: `T$54`
    var ambient: Array<Number>
    var probe: Array<Any>
    var directional: Array<Any>
    var directionalShadow: Array<Any>
    var directionalShadowMap: Array<Any>
    var directionalShadowMatrix: Array<Any>
    var spot: Array<Any>
    var spotShadow: Array<Any>
    var spotShadowMap: Array<Any>
    var spotShadowMatrix: Array<Any>
    var rectArea: Array<Any>
    var point: Array<Any>
    var pointShadow: Array<Any>
    var pointShadowMap: Array<Any>
    var pointShadowMatrix: Array<Any>
    var hemi: Array<Any>
}

open external class WebGLLights(gl: WebGLRenderingContext, properties: Any, info: Any) {
    open var state: `T$55`
    open fun get(light: Any): Any
    open fun setup(lights: Any, shadows: Any, camera: Any)
}