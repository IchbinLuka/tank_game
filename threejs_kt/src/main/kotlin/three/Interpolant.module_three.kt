@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

open external class Interpolant(parameterPositions: Any, sampleValues: Any, sampleSize: Number, resultBuffer: Any = definedExternally) {
    open var parameterPositions: Any
    open var sampleValues: Any
    open var valueSize: Number
    open var resultBuffer: Any
    open fun evaluate(time: Number): Any
}