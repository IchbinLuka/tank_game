package gameobjects

import core.GameObject
import extensions.enableShadows
import extensions.toFloatList
import org.khronos.webgl.Float32Array
import three.DoubleSide
import three.js.*
import util.Log
import util.angleFromAscent
import util.getLinearGradient
import kotlin.math.*

class Ground(
    private val res: Int = 200,
    private val width: Float = 200f,
    private val offset: Vector3 = Vector3(0, 0, 0),
    private val height: Float = 40f,
    private val depth: Float = 20f
) : GameObject() {
    override val renderable: Object3D
        get() {
            Log.d("Generating ground")
            val geometry = BufferGeometry()
            val vertices = mutableListOf<Float>()
            val normals = mutableListOf<Float>()
            for (i in 1 until heightData.size) {
                val height = heightData[i]
                val currentPos = Vector3(i * (width / res), 0, 0).add(offset)
                val currentTop = currentPos.clone().add(Vector3(0, height, 0))
                val currentBack = currentTop.clone().add(Vector3(0, 0, -depth))
                val previous = currentPos.clone().add(Vector3(-(width / res), 0, 0))
                val previousTop = previous.clone().add(Vector3(0, heightData[i-1], 0))
                val previousBack = previousTop.clone().add(Vector3(0, 0, -depth))
                //Log.d("previousTop: ${previousTop.toFloatList()}, previous: ${previous.toFloatList()}" )
                vertices.addAll(currentPos.toFloatList())
                vertices.addAll(previousTop.toFloatList())
                vertices.addAll(currentTop.toFloatList())

                vertices.addAll(previous.toFloatList())
                vertices.addAll(currentPos.toFloatList())
                vertices.addAll(previousTop.toFloatList())

                normals.addAll(arrayOf(0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f))
                normals.addAll(arrayOf(0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f))

                vertices.addAll(previousTop.toFloatList())
                vertices.addAll(currentTop.toFloatList())
                vertices.addAll(previousBack.toFloatList())

                vertices.addAll(currentTop.toFloatList())
                vertices.addAll(previousBack.toFloatList())
                vertices.addAll(currentBack.toFloatList())
                normals.addAll(arrayOf(0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f))
                normals.addAll(arrayOf(0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f))

            }
            geometry.setAttribute("position", BufferAttribute(Float32Array(vertices.toTypedArray()).asDynamic(), 3))
            geometry.setAttribute("normal", BufferAttribute(Float32Array(normals.toTypedArray()).asDynamic(), 3))
            return Mesh(geometry, MeshPhongMaterial().apply {
                color = Color(0x00ff00)
                side = DoubleSide
            })
        }

    private var object3D: Object3D? = null

    private val heightData: Array<Float> = Array<Float>(res) { (height * 0.5 * (1 - sin(0.1 * it))).toFloat() }

    fun heightAt(position: Float): Float {
        val pos = position - offset.x.toFloat()
        if (pos < 0 || pos > width - 1) return 0f
        val normPos = pos * res / width
        val previous = heightData[floor(normPos).toInt()]
        val next = heightData[ceil(normPos).toInt()]
        val progress = 1 - ceil(normPos) + normPos
        return offset.y.toFloat() + getLinearGradient(previous, next, progress)
    }

    private fun averageAscent(start: Int, end: Int): Float {
        return (heightData[end] - heightData[start]) / (end - start)
    }

    fun rotationAt(position: Float): Float {
        val pos = position - offset.x.toFloat()
        if (pos < 0 || pos > width - 1) return 0f
        val normPos = pos * res / width
        // TODO: Fix rotation
        val previous = floor(normPos).toInt()
        val next = ceil(normPos).toInt()
        val startAsc = angleFromAscent(averageAscent(previous - 1, previous) * res / width)
        val endAsc = angleFromAscent(averageAscent(next, next + 1) * res / width)
        Log.d("start: $startAsc, end: $endAsc")
        return getLinearGradient(startAsc, endAsc, normPos - previous)
    }

    override suspend fun initVisual() {
        object3D = Mesh(BoxGeometry(200, 200, 15), MeshPhongMaterial().apply {
            color = Color(0x1F8228)
        }).apply {
            position.y = -100
            enableShadows()
        }
    }
}