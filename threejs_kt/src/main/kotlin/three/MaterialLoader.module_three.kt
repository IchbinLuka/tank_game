@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import org.w3c.xhr.ProgressEvent

external interface `T$46` {
    @nativeGetter
    operator fun get(key: String): Texture?
    @nativeSetter
    operator fun set(key: String, value: Texture)
}

open external class MaterialLoader(manager: LoadingManager = definedExternally) : Loader {
    open var textures: `T$46`
    open fun load(url: String, onLoad: (material: Material) -> Unit, onProgress: (event: ProgressEvent) -> Unit = definedExternally, onError: (event: dynamic /* Error | ErrorEvent */) -> Unit = definedExternally)
    open fun setTextures(textures: `T$46`): MaterialLoader /* this */
    open fun parse(json: Any): Material
}