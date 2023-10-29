@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import org.khronos.webgl.WebGLRenderingContext

external interface WebGLCapabilitiesParameters {
    var precision: String?
        get() = definedExternally
        set(value) = definedExternally
    var logarithmicDepthBuffer: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

open external class WebGLCapabilities(gl: WebGLRenderingContext, extensions: Any, parameters: WebGLCapabilitiesParameters) {
    open var isWebGL2: Boolean
    open var precision: String
    open var logarithmicDepthBuffer: Boolean
    open var maxTextures: Number
    open var maxVertexTextures: Number
    open var maxTextureSize: Number
    open var maxCubemapSize: Number
    open var maxAttributes: Number
    open var maxVertexUniforms: Number
    open var maxVaryings: Number
    open var maxFragmentUniforms: Number
    open var vertexTextures: Boolean
    open var floatFragmentTextures: Boolean
    open var floatVertexTextures: Boolean
    open fun getMaxAnisotropy(): Number
    open fun getMaxPrecision(precision: String): String
}