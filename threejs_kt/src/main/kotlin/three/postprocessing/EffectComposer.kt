@file:JsModule("three/examples/jsm/postprocessing/EffectComposer")
@file:JsNonModule
package three.postprocessing

import three.js.WebGLRenderTarget
import three.js.WebGLRenderer

open external class EffectComposer(
    renderer: WebGLRenderer,
    renderTarget: WebGLRenderTarget = definedExternally
) {
    open var passes: Array<Pass>
    open var readBuffer: dynamic
    open var renderer: WebGLRenderer
    open var renderToScreen: Boolean
    open var writeBuffer: dynamic

    open fun addPass(pass: Pass)
    open fun insertPass(pass: Pass, index: Int)
    open fun isLastEnabledPass(passIndex: Int): Boolean
    open fun removePass(pass: Pass)
    open fun render(deltaTime: Float = definedExternally)
    open fun reset(renderTarget: WebGLRenderTarget = definedExternally)
    open fun setPixelRatio(pixelRatio: Float)
    open fun setSize(width: Int, height: Int)
    open fun swapBuffers()

}