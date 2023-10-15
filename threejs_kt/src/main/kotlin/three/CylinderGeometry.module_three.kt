@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

external interface CylinderGeometryParameters {
    var radiusTop: Number
    var radiusBottom: Number
    var height: Number
    var radialSegments: Number
    var heightSegments: Number
    var openEnded: Boolean
    var thetaStart: Number
    var thetaLength: Number
}

open external class CylinderBufferGeometry(radiusTop: Number = definedExternally, radiusBottom: Number = definedExternally, height: Number = definedExternally, radialSegments: Number = definedExternally, heightSegments: Number = definedExternally, openEnded: Boolean = definedExternally, thetaStart: Number = definedExternally, thetaLength: Number = definedExternally) : BufferGeometry {
    override var type: String
    open var parameters: CylinderGeometryParameters
}

open external class CylinderGeometry(
    radiusTop: Number = definedExternally,
    radiusBottom: Number = definedExternally,
    height: Number = definedExternally,
    radiusSegments: Number = definedExternally,
    heightSegments: Number = definedExternally,
    openEnded: Boolean = definedExternally,
    thetaStart: Number = definedExternally,
    thetaLength: Number = definedExternally
) : BufferGeometry {
    override var type: String
    open var parameters: CylinderGeometryParameters
}