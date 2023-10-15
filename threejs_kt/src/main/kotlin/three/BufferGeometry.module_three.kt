@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import kotlin.js.Json


external interface BufferGeometryGroup {
    var start: Number
    var count: Number
    var materialIndex: Int
}

external interface DrawRange {
    var start: Number
    var count: Number
}

open external class BufferGeometry : EventDispatcher {
    open var id: Number
    open var uuid: String
    open var name: String
    open var type: String
    open var index: BufferAttribute?

    /**
     * Use [setAttribute] and [getAttribute] instead.
     */
    open var attributes: dynamic

    open var morphAttributes: dynamic
    open var morphTargetsRelative: Boolean
    open var groups: Array<BufferGeometryGroup>
    open var boundingBox: Box3?
    open var boundingSphere: Sphere?
    open var drawRange: DrawRange
    open var userData: Json
    open var isBufferGeometry: Boolean
    open fun getIndex(): BufferAttribute?
    open fun setIndex(index: BufferAttribute?)
    open fun setIndex(index: Array<Number>?)
    open fun setAttribute(name: String, attribute: BufferAttribute): BufferGeometry
    open fun setAttribute(name: String, attribute: InterleavedBufferAttribute): BufferGeometry
    open fun getAttribute(name: String): BufferAttribute /* BufferAttribute | InterleavedBufferAttribute */
    open fun deleteAttribute(name: String): BufferGeometry
    open fun addGroup(start: Number, count: Number, materialIndex: Number = definedExternally)
    open fun clearGroups()
    open fun setDrawRange(start: Number, count: Number)
    open fun applyMatrix4(matrix: Matrix4): BufferGeometry
    open fun rotateX(angle: Number): BufferGeometry
    open fun rotateY(angle: Number): BufferGeometry
    open fun rotateZ(angle: Number): BufferGeometry
    open fun translate(x: Number, y: Number, z: Number): BufferGeometry
    open fun scale(x: Number, y: Number, z: Number): BufferGeometry
    open fun lookAt(v: Vector3)
    open fun center(): BufferGeometry
    open fun setFromObject(obj: Object3D): BufferGeometry
    open fun setFromPoints(points: Array<Vector3>): BufferGeometry
    open fun setFromPoints(points: Array<Vector2>): BufferGeometry
    open fun updateFromObject(obj: Object3D)
    open fun computeBoundingBox()
    open fun computeBoundingSphere()
    open fun computeVertexNormals()
    open fun merge(geometry: BufferGeometry, offset: Number = definedExternally): BufferGeometry
    open fun normalizeNormals()
    open fun toNonIndexed(): BufferGeometry
    open fun toJSON(): Any
    open fun clone(): BufferGeometry /* this */
    open fun copy(source: BufferGeometry): BufferGeometry /* this */
    open fun dispose()
    open var drawcalls: Any
    open var offsets: Any
    open fun addIndex(index: Any)
    open fun addDrawCall(start: Any, count: Any, indexOffset: Any = definedExternally)
    open fun clearDrawCalls()
    open fun addAttribute(name: String, attribute: BufferAttribute): BufferGeometry
    open fun addAttribute(name: String, attribute: InterleavedBufferAttribute): BufferGeometry
    open fun removeAttribute(name: String): BufferGeometry
    open fun addAttribute(name: Any, array: Any, itemSize: Any): Any

    companion object {
        var MaxIndex: Number
    }
}