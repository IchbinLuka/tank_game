@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

external interface RingGeometryParameters {
    var innerRadius: Number
    var outerRadius: Number
    var thetaSegments: Number
    var phiSegments: Number
    var thetaStart: Number
    var thetaLength: Number
}

open external class RingGeometry(
    innerRadius: Number = definedExternally,
    outerRadius: Number = definedExternally,
    thetaSegments: Number = definedExternally,
    phiSegments: Number = definedExternally,
    thetaStart: Number = definedExternally,
    thetaLength: Number = definedExternally
) : BufferGeometry {
    override var type: String
    open var parameters: RingGeometryParameters
}