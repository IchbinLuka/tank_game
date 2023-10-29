@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

open external class HemisphereLight : Light {
    constructor(skyColor: Color = definedExternally, groundColor: Color = definedExternally, intensity: Number = definedExternally)
    constructor(skyColor: Color = definedExternally, groundColor: String = definedExternally, intensity: Number = definedExternally)
    constructor(skyColor: Color = definedExternally, groundColor: Number = definedExternally, intensity: Number = definedExternally)
    constructor(skyColor: String = definedExternally, groundColor: Color = definedExternally, intensity: Number = definedExternally)
    constructor(skyColor: String = definedExternally, groundColor: String = definedExternally, intensity: Number = definedExternally)
    constructor(skyColor: String = definedExternally, groundColor: Number = definedExternally, intensity: Number = definedExternally)
    constructor(skyColor: Number = definedExternally, groundColor: Color = definedExternally, intensity: Number = definedExternally)
    constructor(skyColor: Number = definedExternally, groundColor: String = definedExternally, intensity: Number = definedExternally)
    constructor(skyColor: Number = definedExternally, groundColor: Number = definedExternally, intensity: Number = definedExternally)
    override var type: String
    override var position: Vector3
    override var castShadow: Boolean
    open var groundColor: Color
    open var isHemisphereLight: Boolean
}