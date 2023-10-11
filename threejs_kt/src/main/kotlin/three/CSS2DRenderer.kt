@file:JsModule("three/examples/jsm/renderers/CSS2DRenderer")
@file:JsNonModule

package three

import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import three.js.Camera
import three.js.Object3D
import three.js.Scene

open external class CSS2DRenderer(parameters: dynamic = definedExternally) {
    val domElement: HTMLElement = definedExternally

    fun getSize(): dynamic

    fun setSize(width: Number, height: Number)

    fun render(scene: Scene, camera: Camera)

}

open external class CSS2DObject(element: Element) : Object3D {
    var element: HTMLElement
    val isCSS2DObject: Boolean = definedExternally
}