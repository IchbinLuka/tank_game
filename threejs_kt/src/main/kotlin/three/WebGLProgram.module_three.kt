@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

open external class WebGLProgram(renderer: WebGLRenderer, cacheKey: String, parameters: Any?) {
    open var name: String
    open var id: Number
    open var cacheKey: String
    open var usedTimes: Number
    open var program: Any
    open var vertexShader: WebGLShader
    open var fragmentShader: WebGLShader
    open var uniforms: Any
    open var attributes: Any
    open fun getUniforms(): WebGLUniforms
    open fun getAttributes(): Any
    open fun destroy()
}