@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

open external class LinearInterpolant(parameterPositions: Any, samplesValues: Any, sampleSize: Number, resultBuffer: Any = definedExternally) : Interpolant {
    open fun interpolate_(i1: Number, t0: Number, t: Number, t1: Number): Any
}