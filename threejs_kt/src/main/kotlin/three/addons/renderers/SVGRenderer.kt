@file:JsModule("three/examples/jsm/renderers/SVGRenderer")
@file:JsNonModule

package three.addons.renderers

import org.w3c.dom.Node
import three.js.Camera
import three.js.Color
import three.js.Scene

open external class SVGObject {
    open val isSVGObject: Boolean
    open var node: Node
}

open external class SVGRenderer {
    open var overdraw: Number

    open fun clear()
    open fun getSize(): Size
    open fun render(scene: Scene, camera: Camera)
    open fun setClearColor(color: Color, alpha: Number)
    open fun setPrecision(precision: Number)
    open fun setQuality(quality: Number)
    open fun setSize(width: Number, height: Number)
}