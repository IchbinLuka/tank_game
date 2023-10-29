@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

open external class SkeletonHelper(obj: Object3D) : LineSegments<dynamic, dynamic> {
    override var type: String
    open var bones: Array<Bone>
    open var root: Object3D
    open var isSkeletonHelper: Boolean
    override var matrix: Matrix4
    override var matrixAutoUpdate: Boolean
    open fun getBoneList(obj: Object3D): Array<Bone>
    open fun update()
}