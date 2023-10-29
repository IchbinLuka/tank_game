@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

open external class AmbientLightProbe : LightProbe {
    constructor(color: Color = definedExternally, intensity: Number = definedExternally)
    constructor(color: String = definedExternally, intensity: Number = definedExternally)
    constructor(color: Number = definedExternally, intensity: Number = definedExternally)
    open var isAmbientLightProbe: Boolean
}