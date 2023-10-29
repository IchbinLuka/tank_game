@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import org.khronos.webgl.WebGLBuffer
import org.khronos.webgl.WebGLRenderingContext

external interface `T$53` {
    var buffer: WebGLBuffer
    var type: GLenum
    var bytesPerElement: Number
    var version: Number
}

open external class WebGLAttributes(gl: WebGLRenderingContext, capabilities: WebGLCapabilities) {
    open fun get(attribute: BufferAttribute): `T$53`
    open fun get(attribute: InterleavedBufferAttribute): `T$53`
    open fun remove(attribute: BufferAttribute)
    open fun remove(attribute: InterleavedBufferAttribute)
    open fun update(attribute: BufferAttribute, bufferType: GLenum)
    open fun update(attribute: InterleavedBufferAttribute, bufferType: GLenum)
}