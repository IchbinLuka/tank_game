@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

open external class AudioListener : Object3D {
    override var type: String /* 'AudioListener' */
    open var context: AudioContext
    open var gain: GainNode
    open var filter: Any?
    open var timeDelta: Number
    open fun getInput(): GainNode
    open fun removeFilter(): AudioListener /* this */
    open fun setFilter(value: Any): AudioListener /* this */
    open fun getFilter(): Any
    open fun setMasterVolume(value: Number): AudioListener /* this */
    open fun getMasterVolume(): Number
    override fun updateMatrixWorld(force: Boolean)
}