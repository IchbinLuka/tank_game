@file:JsModule("three/examples/jsm/postprocessing/SSAOPass")
@file:JsNonModule

package three.addons.postprocessing

import three.js.*

open external class SSAOPass(
    scene: Scene,
    camera: Camera,
    width: Number,
    height: Number
) : Pass {
    open var width: Number
    open var height: Number
    open var camera: Camera
    open var scene: Scene
    open var kernelRadius: Number
    open var kernelSize: Number
    open var kernel: Array<dynamic>
    open var noiseTexture: Texture?
    open var output: Number
    open var minDistance: Number
    open var maxDistance: Number
    open var beautyRenderTarget: WebGLRenderTarget
    open var normalRenderTarget: WebGLRenderTarget
    open var ssaoRenderTarget: WebGLRenderTarget
    open var blurRenderTarget: WebGLRenderTarget
    open var ssaoMaterial: ShaderMaterial
    open var normalMaterial: MeshNormalMaterial
    open var blurMaterial: ShaderMaterial
    open var depthRenderMaterial: ShaderMaterial
    open var copyMaterial: ShaderMaterial
    open var fsQuad: dynamic
    open var originalClearColor: Color

    fun dispose()
    fun render(renderer: Renderer, writeBuffer: dynamic)
    fun renderPass(renderer: Renderer, renderTarget: RenderTarget, clearColor: Color, clearAlpha: Boolean)
    fun renderOverride(renderer: Renderer, overrideMaterial: Material, renderTarget: RenderTarget, clearColor: Color, clearAlpha: Boolean)
    fun setSize(width: Number, height: Number)
    fun generateSampleKernel()
    fun generateRandomKernelRotations()
    fun overrideVisibility()
    fun restoreVisibility()

}