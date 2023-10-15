@file:JsModule("three/examples/jsm/renderers/CSS3DRenderer")
@file:JsNonModule

package three.addons.renderers

import org.w3c.dom.HTMLElement
import three.js.PerspectiveCamera
import three.js.Scene

external interface CSS3DRendererParameters {
    var element: HTMLElement
}

external interface Size {
    var width: Number
    var height: Number
}

open external class CSS3DObject(element: HTMLElement = definedExternally) {
    var element: HTMLElement
    val isCSS3DObject: Boolean = definedExternally
}

open external class CSS3DRenderer(
    parameters: CSS3DRendererParameters = definedExternally
) {
    val domElement: HTMLElement = definedExternally

    fun getSize(): Size

    fun setSize(width: Number, height: Number)

    fun render(scene: Scene, camera: PerspectiveCamera)

}