@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

open external class LineSegments<TGeometry, TMaterial>(geometry: TGeometry = definedExternally, material: TMaterial = definedExternally, mode: Number = definedExternally) : Line<TGeometry, TMaterial> {
    override var type: dynamic /* String | String */
    open var isLineSegments: Boolean
}
