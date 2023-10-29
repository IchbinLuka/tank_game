@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import org.khronos.webgl.WebGLRenderingContext

external interface `T$13` {
    var geometries: Number
    var textures: Number
}

external interface `T$14` {
    var calls: Number
    var frame: Number
    var lines: Number
    var points: Number
    var triangles: Number
}

open external class WebGLInfo(gl: WebGLRenderingContext) {
    open var autoReset: Boolean
    open var memory: `T$13`
    open var programs: Array<WebGLProgram>?
    open var render: `T$14`
    open fun update(count: Number, mode: GLenum, instanceCount: Number)
    open fun reset()
}