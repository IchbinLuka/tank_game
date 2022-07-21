@file:JsModule("three/examples/jsm/postprocessing/UnrealBloomPass")
@file:JsNonModule
package three.postprocessing

import three.js.*

open external class UnrealBloomPass(
    resolution: Vector2 = definedExternally,
    strength: Int = definedExternally,
    radius: Number,
    threshold: Number
): Pass {
    open var clearColor: Color
    open var radius: Number
    open var threshold: Number
    open var resolution: Vector2
    open var renderTargetBright: WebGLRenderTarget
    open var highPassUniforms: WebGLUniforms
    open var materialHighPassFilter: ShaderMaterial
    open var separableBlurMaterials: Array<ShaderMaterial>
    open var compositeMaterial: ShaderMaterial
    open var bloomTintColors: Array<Vector3>
    open var copyUniforms: WebGLUniforms
    open var oldClearAlpha: Int
    open var basic: MeshBasicMaterial
    open var fsQuad: dynamic

    open fun dispose()

    open fun getSeparateBlurMaterial(kernelRadius: Number): ShaderMaterial
    open fun getCompositeMaterial(nMips: Int): ShaderMaterial


}