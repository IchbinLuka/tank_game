@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

open external class CubeCamera(near: Number, far: Number, renderTarget: WebGLCubeRenderTarget) : Object3D {
    override var type: String /* 'CubeCamera' */
    open var renderTarget: WebGLCubeRenderTarget
    open fun update(renderer: WebGLRenderer, scene: Scene)
    open fun clear(renderer: WebGLRenderer, color: Boolean, depth: Boolean, stencil: Boolean)
}