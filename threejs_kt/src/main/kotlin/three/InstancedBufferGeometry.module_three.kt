@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

external interface `T$47` {
    var start: Number
    var count: Number
    var instances: Number
}

open external class InstancedBufferGeometry : BufferGeometry {
    override var type: String
    override var groups: Array<`T$47`>
    open var instanceCount: Number
    override fun addGroup(start: Number, count: Number, instances: Number)
}