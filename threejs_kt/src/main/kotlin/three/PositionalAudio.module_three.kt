@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

open external class PositionalAudio(listener: AudioListener) : Audio<PannerNode> {
    open var panner: PannerNode
    override fun getOutput(): PannerNode
    open fun setRefDistance(value: Number): PositionalAudio /* this */
    open fun getRefDistance(): Number
    open fun setRolloffFactor(value: Number): PositionalAudio /* this */
    open fun getRolloffFactor(): Number
    open fun setDistanceModel(value: String /* "exponential" | "inverse" | "linear" */): PositionalAudio /* this */
    open fun getDistanceModel(): String /* "exponential" | "inverse" | "linear" */
    open fun setMaxDistance(value: Number): PositionalAudio /* this */
    open fun getMaxDistance(): Number
    open fun setDirectionalCone(coneInnerAngle: Number, coneOuterAngle: Number, coneOuterGain: Number): PositionalAudio /* this */
    override fun updateMatrixWorld(force: Boolean)
}