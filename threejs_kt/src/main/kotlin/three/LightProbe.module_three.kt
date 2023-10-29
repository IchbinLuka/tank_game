@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

open external class LightProbe(sh: SphericalHarmonics3 = definedExternally, intensity: Number = definedExternally) : Light {
    override var type: String
    open var isLightProbe: Boolean
    open var sh: SphericalHarmonics3
    open fun fromJSON(json: Any?): LightProbe
}