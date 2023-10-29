@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

open external class Sprite(material: SpriteMaterial = definedExternally) : Object3D {
    override var type: String /* 'Sprite' */
    open var isSprite: Boolean
    open var geometry: BufferGeometry
    open var material: SpriteMaterial
    open var center: Vector2
    override fun raycast(raycaster: Raycaster, intersects: Array<Intersection>)
    open fun copy(source: Sprite /* this */): Sprite /* this */
}