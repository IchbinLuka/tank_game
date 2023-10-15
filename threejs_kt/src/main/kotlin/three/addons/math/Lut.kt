@file:JsModule("three/examples/jsm/math/Lut")
@file:JsNonModule

package three.addons.math


import org.w3c.dom.HTMLCanvasElement
import three.js.Color

open external class Lut(
    colormap: String, count: Number
) {
    open var lut: Array<Color>
    open var map: Array<Color>
    open var minV: Number
    open var maxV: Number
    open var n: Number

    open fun copy(lut: Lut): Lut

    open fun addColorMap(name: String, arrayOfColors: Array<Color>): Lut
    open fun createCanvas(): HTMLCanvasElement
    open fun getColor(alpha: Number): Color
    open fun setColorMap(colormap: String, count: Number = definedExternally): Lut

    open fun setMin(minV: Number): Lut
    open fun setMax(maxV: Number): Lut

    open fun updateCanvas(canvas: HTMLCanvasElement): HTMLCanvasElement
}