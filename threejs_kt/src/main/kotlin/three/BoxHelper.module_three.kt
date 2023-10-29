@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

open external class BoxHelper : LineSegments<dynamic, dynamic> {
    constructor(obj: Object3D, color: Color = definedExternally)
    constructor(obj: Object3D, color: String = definedExternally)
    constructor(obj: Object3D, color: Number = definedExternally)
    override var type: String
    open fun update(obj: Object3D = definedExternally)
    open fun setFromObject(obj: Object3D): BoxHelper /* this */
}