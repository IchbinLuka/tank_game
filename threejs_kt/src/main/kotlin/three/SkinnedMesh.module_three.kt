@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

open external class SkinnedMesh<TGeometry : BufferGeometry, TMaterial : Material>(
    geometry: TGeometry = definedExternally,
    material: TMaterial = definedExternally,
    useVertexTexture: Boolean = definedExternally
) : Mesh<TGeometry, TMaterial> {
    open var bindMode: String
    open var bindMatrix: Matrix4
    open var bindMatrixInverse: Matrix4
    open var skeleton: Skeleton
    open var isSkinnedMesh: Boolean
    open fun bind(skeleton: Skeleton, bindMatrix: Matrix4 = definedExternally)
    open fun pose()
    open fun normalizeSkinWeights()
    override fun updateMatrixWorld(force: Boolean)
}