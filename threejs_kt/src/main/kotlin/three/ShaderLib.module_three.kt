@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

external interface `T$8` {
    @nativeGetter
    operator fun get(uniform: String): IUniform?
    @nativeSetter
    operator fun set(uniform: String, value: IUniform)
}

external interface Shader {
    var uniforms: `T$8`
    var vertexShader: String
    var fragmentShader: String
}