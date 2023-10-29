@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

open external class RectAreaLight : Light {
    constructor(color: Color = definedExternally, intensity: Number = definedExternally, width: Number = definedExternally, height: Number = definedExternally)
    constructor(color: String = definedExternally, intensity: Number = definedExternally, width: Number = definedExternally, height: Number = definedExternally)
    constructor(color: Number = definedExternally, intensity: Number = definedExternally, width: Number = definedExternally, height: Number = definedExternally)
    override var type: String
    open var width: Number
    open var height: Number
    override var intensity: Number
    open var isRectAreaLight: Boolean
}