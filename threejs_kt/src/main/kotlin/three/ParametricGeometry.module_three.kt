@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

external interface ParametricGeometryParameters {
    var func: (u: Number, v: Number, dest: Vector3) -> Unit
    var slices: Number
    var stacks: Number
}

open external class ParametricGeometry(
    func: (u: Number, v: Number, dest: Vector3) -> Unit,
    slices: Number,
    stacks: Number
) : BufferGeometry {
    override var type: String
    open var parameters: ParametricGeometryParameters
}