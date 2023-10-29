@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import org.khronos.webgl.WebGLRenderingContext

open external class WebGLBindingStates(gl: WebGLRenderingContext, extensions: WebGLExtensions, attributes: WebGLAttributes, capabilities: WebGLCapabilities) {
    open fun setup(obj: Object3D, material: Material, program: WebGLProgram, geometry: BufferGeometry, index: BufferAttribute)
    open fun reset()
    open fun resetDefaultState()
    open fun dispose()
    open fun releaseStatesOfGeometry()
    open fun releaseStatesOfProgram()
    open fun initAttributes()
    open fun enableAttribute(attribute: Number)
    open fun disableUnusedAttributes()
}