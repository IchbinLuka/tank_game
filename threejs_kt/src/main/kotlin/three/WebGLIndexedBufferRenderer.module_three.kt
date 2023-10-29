@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import org.khronos.webgl.WebGLRenderingContext

open external class WebGLIndexedBufferRenderer(gl: WebGLRenderingContext, extensions: Any, info: Any, capabilities: Any) {
    open fun setMode(value: Any)
    open fun setIndex(index: Any)
    open fun render(start: Any, count: Number)
    open fun renderInstances(start: Any, count: Number, primcount: Number)
}