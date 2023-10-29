@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

open external class PointLight : Light {
    constructor(color: Color = definedExternally, intensity: Number = definedExternally, distance: Number = definedExternally, decay: Number = definedExternally)
    constructor(color: String = definedExternally, intensity: Number = definedExternally, distance: Number = definedExternally, decay: Number = definedExternally)
    constructor(color: Number = definedExternally, intensity: Number = definedExternally, distance: Number = definedExternally, decay: Number = definedExternally)
    override var type: String
    override var intensity: Number
    open var distance: Number
    open var decay: Number
    override var shadow: PointLightShadow
    open var power: Number
}