@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

open external class StereoCamera : Camera {
    override var type: String /* 'StereoCamera' */
    open var aspect: Number
    open var eyeSep: Number
    open var cameraL: PerspectiveCamera
    open var cameraR: PerspectiveCamera
    open fun update(camera: PerspectiveCamera)
}