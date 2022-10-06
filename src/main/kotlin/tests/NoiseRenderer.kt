package tests

import core.HeightMap
import ext.TAG
import kotlinx.browser.document
import kotlinx.dom.appendElement
import org.khronos.webgl.Uint8ClampedArray
import org.w3c.dom.*
import util.Log

class HeightmapRenderer {

    private val canvas: HTMLCanvasElement = document.body!!.appendElement("canvas") {} as HTMLCanvasElement

    private val context = canvas.getContext("2d") as CanvasRenderingContext2D?

    fun render(data: HeightMap) {
        if (context == null) return
        val imgData = context.createImageData(data.size.toDouble(), data[0].size.toDouble())
        Log.v(TAG, "Dimensions: ${data.size} x ${data[0].size}")
        val max: Double = data.maxOf { it.max() }
        data.forEachIndexed {
            i, doubles -> doubles.forEachIndexed {
                j, d ->
                val value = Array<Byte>(4) { ((d / max) * 128).toInt().toByte() }
                imgData.data.set(Uint8ClampedArray(value), data[0].size * j + i)
            }
        }

        Log.v(TAG, "Length: ${imgData.data.length}")
        /*createImageBitmap(imgData).then {
            context.drawImage(it, 0.0, 0.0)
        }*/
        context.putImageData(imgData, 0.0, 0.0)
    }
}