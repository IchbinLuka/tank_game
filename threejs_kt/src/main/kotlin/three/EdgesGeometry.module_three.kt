@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

external interface EdgesGeometryParameters {
    var thresholdAngle: Number
}

open external class EdgesGeometry(geometry: BufferGeometry, thresholdAngle: Number = definedExternally) :
    BufferGeometry {
    override var type: String
    open var parameters: EdgesGeometryParameters
}