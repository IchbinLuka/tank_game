@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

external interface `T$20` {
    @nativeGetter
    operator fun get(key: String): Number?
    @nativeSetter
    operator fun set(key: String, value: Number)
}

open external class Mesh<TGeometry : BufferGeometry, TMaterial : Material>(
    geometry: TGeometry = definedExternally,
    material: TMaterial = definedExternally
) : Object3D {
    open var geometry: TGeometry
    open var material: TMaterial
    open var morphTargetInfluences: Array<Number>
    open var morphTargetDictionary: `T$20`
    open var isMesh: Boolean
    override var type: String
    open fun updateMorphTargets()
    override fun raycast(raycaster: Raycaster, intersects: Array<Intersection>)
}
