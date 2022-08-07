package gameobjects

import core.GameObject
import ext.enableShadows
import ext.toFloatList
import org.khronos.webgl.Float32Array
import three.js.*
import util.Log
import util.angleFromAscent
import util.getLinearGradient
import kotlin.math.*

class Ground(
    private val res: Int = 200,
    private val width: Double = 200.0,
    private val offset: Vector3 = Vector3(0, 0, 0),
    private val height: Double = 40.0,
    private val depth: Double = 20.0
) : GameObject() {
    override val renderable: Object3D
        get() {
            Log.d(TAG, "Generating ground")
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
                currentPos.add(Vector3(0, 0, 3))
                previous.add(Vector3(0, 0, 3))
                //Log.d("previousTop: ${previousTop.toFloatList()}, previous: ${previous.toFloatList()}" )
                vertices.addAll(currentPos.toFloatList())
                vertices.addAll(currentTop.toFloatList())
                vertices.addAll(previousTop.toFloatList())

                vertices.addAll(previous.toFloatList())
                vertices.addAll(currentPos.toFloatList())
                vertices.addAll(previousTop.toFloatList())

                normals.addAll(arrayOf(0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f))
                normals.addAll(arrayOf(0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f))

                vertices.addAll(previousTop.toFloatList())
                vertices.addAll(currentTop.toFloatList())
                vertices.addAll(previousBack.toFloatList())

                vertices.addAll(currentTop.toFloatList())
                vertices.addAll(currentBack.toFloatList())
                vertices.addAll(previousBack.toFloatList())
                normals.addAll(arrayOf(0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f))
                normals.addAll(arrayOf(0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f))

            }
            geometry.setAttribute("position", BufferAttribute(Float32Array(vertices.toTypedArray()).asDynamic(), 3))
            geometry.setAttribute("normal", BufferAttribute(Float32Array(normals.toTypedArray()).asDynamic(), 3))
            return Mesh(geometry, MeshPhongMaterial().apply {
                color = Color(0x195917)
                //side = DoubleSide
                shininess = 50
            })
        }

    private var object3D: Object3D? = null

    private val heightData: Array<Double> = Array(res) { (height * 0.2 * (1 - sin(0.5 * it))) + height / 2 }

    fun heightAt(position: Double): Double {
        val pos = position - offset.x.toFloat()
        if (pos < 0 || pos > width - 1) return 0.0
        val normPos = pos * res / width
        val previous = heightData[floor(normPos).toInt()]
        val next = heightData[ceil(normPos).toInt()]
        val progress = 1 - ceil(normPos) + normPos
        return offset.y.toFloat() + getLinearGradient(previous, next, progress)
    }

    private fun averageAscent(start: Int, end: Int): Double {
        return (heightData[end] - heightData[start]) / (end - start)
    }

    fun rotationAt(position: Double): Double {
        val pos = position - offset.x.toFloat()
        if (pos < 0 || pos > width - 1) return 0.0
        val normPos = pos * res / width
        val previous = floor(normPos).toInt()
        val next = ceil(normPos).toInt()
        val nearest = normPos.roundToInt()
        val currentAngle = angleFromAscent(averageAscent(previous, next) * res / width)
        val nearestAsc = (res / width) * if (nearest == previous) {
            averageAscent(previous - 1, previous)
        } else {
            averageAscent(next, next + 1)
        }
        var progress = 0.5f + normPos - nearest
        if (normPos - nearest < 0) {
            progress = 1 - progress // Invert progress if nearest is right
        }
        return getLinearGradient(angleFromAscent(nearestAsc), currentAngle, progress)
    }

    override suspend fun initVisual() {
        object3D = Mesh(BoxGeometry(200, 200, 15), MeshPhongMaterial().apply {
            color = Color(0x1F8228)
        }).apply {
            position.y = -100
            enableShadows()
        }
    }

    companion object {
        const val TAG = "Ground"
    }
}