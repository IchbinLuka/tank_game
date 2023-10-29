@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import org.khronos.webgl.Uint8Array

open external class AudioAnalyser(audio: Audio<AudioNode>, fftSize: Number = definedExternally) {
    open var analyser: AnalyserNode
    open var data: Uint8Array
    open fun getFrequencyData(): Uint8Array
    open fun getAverageFrequency(): Number
    open fun getData(file: Any): Any
}