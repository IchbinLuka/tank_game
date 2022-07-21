@file:JsModule("three/examples/jsm/postprocessing/RenderPass")
@file:JsNonModule
package three.postprocessing

import three.js.*

open external class RenderPass(
    scene: Scene,
    camera: Camera,
    overrideMaterial: Material,
    clearColor: Color,
    clearAlpha: Int = definedExternally
) : Pass {

    open var clearDepth: Boolean = definedExternally
    open var scene: Scene
    open var camera: Camera
    open var overrideMaterial: Material
    open var clearColor: Color


    fun render(
        renderer: Renderer,
        writeBuffer: dynamic,
        readBuffer: dynamic,
    )
}