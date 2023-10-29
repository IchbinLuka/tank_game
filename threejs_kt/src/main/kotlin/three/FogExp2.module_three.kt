@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

open external class FogExp2 : IFog {
    constructor(hex: Number, density: Number = definedExternally)
    constructor(hex: String, density: Number = definedExternally)
    override var name: String
    override var color: Color
    open var density: Number
    open var isFogExp2: Boolean
    override fun clone(): FogExp2 /* this */
    override fun toJSON(): Any
}