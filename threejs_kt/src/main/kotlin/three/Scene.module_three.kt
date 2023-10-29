@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

open external class Scene : Object3D {
    override var type: String /* 'Scene' */
    open var fog: IFog?
    open var overrideMaterial: Material?
    open var autoUpdate: Boolean
    open var background: dynamic /* Color? | Texture? | WebGLCubeRenderTarget? */
    open var environment: Texture?
    open var isScene: Boolean
    open fun toJSON(meta: Any = definedExternally): Any
    override fun toJSON(meta: `T$0`): Any
    open fun dispose()
}