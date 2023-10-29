@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

open external class WebGLShadowMap(_renderer: WebGLRenderer, _objects: WebGLObjects, maxTextureSize: Number) {
    open var enabled: Boolean
    open var autoUpdate: Boolean
    open var needsUpdate: Boolean
    open var type: ShadowMapType
    open fun render(shadowsArray: Array<Light>, scene: Scene, camera: Camera)
    open var cullFace: Any
}