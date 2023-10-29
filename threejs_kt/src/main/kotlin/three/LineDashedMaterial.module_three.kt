@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

external interface LineDashedMaterialParameters : LineBasicMaterialParameters {
    var scale: Number?
        get() = definedExternally
        set(value) = definedExternally
    var dashSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var gapSize: Number?
        get() = definedExternally
        set(value) = definedExternally
}

open external class LineDashedMaterial(parameters: LineDashedMaterialParameters = definedExternally) : LineBasicMaterial {
    override var type: String
    open var scale: Number
    open var dashSize: Number
    open var gapSize: Number
    open var isLineDashedMaterial: Boolean
    open fun setValues(parameters: LineDashedMaterialParameters)
    override fun setValues(parameters: LineBasicMaterialParameters)
}