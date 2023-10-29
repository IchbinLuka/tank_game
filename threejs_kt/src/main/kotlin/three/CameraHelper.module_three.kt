@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

external interface `T$51` {
    @nativeGetter
    operator fun get(id: String): Array<Number>?
    @nativeSetter
    operator fun set(id: String, value: Array<Number>)
}

open external class CameraHelper(camera: Camera) : LineSegments<dynamic, dynamic> {
    open var camera: Camera
    open var pointMap: `T$51`
    override var type: String
    open fun update()
}