@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

open external class Camera : Object3D {
    open var matrixWorldInverse: Matrix4
    open var projectionMatrix: Matrix4
    open var projectionMatrixInverse: Matrix4
    open var isCamera: Boolean
    override fun getWorldDirection(target: Vector3): Vector3
    override fun updateMatrixWorld(force: Boolean)
}