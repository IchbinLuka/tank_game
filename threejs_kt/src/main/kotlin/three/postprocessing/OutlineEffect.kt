@file:JsModule("three/examples/jsm/effects/OutlineEffect")
@file:JsNonModule

package three.postprocessing

import org.w3c.dom.HTMLCanvasElement
import three.js.*

open external class OutlineEffect(renderer: Renderer, parameters: dynamic = definedExternally) : Renderer {
    var enabled: Boolean
    var autoClear: Boolean
    override var domElement: HTMLCanvasElement
    var shadowMap: dynamic


    override fun render(scene: Object3D, camera: Camera)
    fun renderOutline(scene: Scene, camera: Camera)
    fun clear(color: Color, depth: dynamic, stencil: dynamic)
    fun getPixelRatio(): Number
    fun setPixelRatio(value: Number)

    fun getSize(target: Vector2): Vector2
    override fun setSize(width: Number, height: Number, updateStyle: dynamic)

    fun setViewport(x: Number, y: Number, width: Number, height: Number)
    fun setScissor(x: Number, y: Number, width: Number, height: Number)
    fun setScissorTest(boolean: Boolean)
    fun setRenderTarget(renderTarget: RenderTarget)

}