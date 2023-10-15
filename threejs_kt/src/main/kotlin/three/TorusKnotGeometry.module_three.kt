@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

external interface TorusKnotGeometryParameters {
    var radius: Number
    var tube: Number
    var tubularSegments: Number
    var radialSegments: Number
    var p: Number
    var q: Number
}

open external class TorusKnotGeometry(
    radius: Number = definedExternally,
    tube: Number = definedExternally,
    tubularSegments: Number = definedExternally,
    radialSegments: Number = definedExternally,
    p: Number = definedExternally,
    q: Number = definedExternally
) : BufferGeometry {
    override var type: String
    open var parameters: TorusKnotGeometryParameters
}