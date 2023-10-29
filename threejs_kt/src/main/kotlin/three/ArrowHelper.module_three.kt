@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

open external class ArrowHelper(dir: Vector3, origin: Vector3 = definedExternally, length: Number = definedExternally, color: Number = definedExternally, headLength: Number = definedExternally, headWidth: Number = definedExternally) : Object3D {
    override var type: String
    open var line: Line<dynamic, dynamic>
    open var cone: Mesh<dynamic, dynamic>
    open fun setDirection(dir: Vector3)
    open fun setLength(length: Number, headLength: Number = definedExternally, headWidth: Number = definedExternally)
    open fun setColor(color: Color)
    open fun setColor(color: String)
    open fun setColor(color: Number)
}