@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import org.khronos.webgl.WebGLRenderingContext

open external class WebGLUniforms(gl: WebGLRenderingContext, program: WebGLProgram) {
    open fun setValue(gl: WebGLRenderingContext, name: String, value: Any, textures: WebGLTextures)
    open fun setOptional(gl: WebGLRenderingContext, obj: Any, name: String)

    companion object {
        fun upload(gl: WebGLRenderingContext, seq: Any, values: Array<Any>, textures: WebGLTextures)
        fun seqWithValue(seq: Any, values: Array<Any>): Array<Any>
    }
}