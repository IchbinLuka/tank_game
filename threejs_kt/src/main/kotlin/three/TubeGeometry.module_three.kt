@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

external interface TubeGeometryParameters {
    var path: Curve<Vector3>
    var tubularSegments: Number
    var radius: Number
    var radialSegments: Number
    var closed: Boolean
}

open external class TubeGeometry(
    path: Curve<Vector3>,
    tubularSegments: Number = definedExternally,
    radius: Number = definedExternally,
    radiusSegments: Number = definedExternally,
    closed: Boolean = definedExternally
) : BufferGeometry {
    open var parameters: TubeGeometryParameters
    open var tangents: Array<Vector3>
    open var normals: Array<Vector3>
    open var binormals: Array<Vector3>
}