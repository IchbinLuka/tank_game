@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

open external class Line<TGeometry, TMaterial>(geometry: TGeometry = definedExternally, material: TMaterial = definedExternally, mode: Number = definedExternally) : Object3D {
    open var geometry: TGeometry
    open var material: TMaterial
    override var type: dynamic /* String | String | String | String */
    open var isLine: Boolean
    open var morphTargetInfluences: Array<Number>
    open var morphTargetDictionary: `T$20`
    open fun computeLineDistances(): Line<TGeometry, TMaterial> /* this */
    override fun raycast(raycaster: Raycaster, intersects: Array<Intersection>)
    open fun updateMorphTargets()
}
