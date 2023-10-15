@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import org.khronos.webgl.WebGLRenderingContext

open external class WebGLGeometries(gl: WebGLRenderingContext, attributes: WebGLAttributes, info: WebGLInfo) {
    open fun get(obj: Object3D, geometry: BufferGeometry): BufferGeometry

    open fun update(geometry: BufferGeometry)

    open fun getWireframeAttribute(geometry: BufferGeometry): BufferAttribute
}