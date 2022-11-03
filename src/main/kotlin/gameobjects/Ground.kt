package gameobjects

import core.HeightMap
import core.gameobjects.GameObject
import ext.plus
import ext.toFloatList
import org.khronos.webgl.Float32Array
import three.DoubleSide
import three.js.*
import util.Log
import util.angleFromAscent
import util.getLinearGradient
import util.noise.GroundNoise
import kotlin.math.*

class Ground(
    val width: Int = 200,
    private val offset: Vector3 = Vector3(0, 0, 0),
    height: Double = 40.0,
    depth: Int = 20,
    showVertex: Boolean = false,
    private val heightScale: Double = 1.0,
    private val pointDistance: Double = 1.0,
    data: HeightMap = GroundNoise(width = width, depth = depth, min = -height).allValues()
) : GameObject() {

    override val renderable: Object3D by lazy {
        Group().apply {
            add(Mesh(geometry, MeshPhongMaterial().apply {
                color = Color(0x195917)
                side = DoubleSide
                shininess = 30
                flatShading = true
            }))
            if (showVertex) {
                add(Points(geometry, PointsMaterial().apply {
                    color = Color(0xffffff)
                    size = 0.25
                }))
            }
        }
    }
    private val geometry = BufferGeometry()

    // private val heightData: Array<Double> = Array(res) { (height * 0.2 * (1 - sin(0.5 * it))) + height / 2 }
    private var vertices = Array<Array<Vector3>>(data.size) { i ->
        Array<Vector3>(data[i].size) { j ->
            Vector3(
                i * pointDistance,
                data[i][j] * heightScale,
                j * pointDistance
            ) + offset
        }
    }

    override suspend fun initVisual() {
        updateMesh()
    }

    //region PUBLIC
    fun destroy(position: Double, strength: Double) {
        // TODO
        val function: (Double) -> Double = {
            if (it in 0.0..PI) {
                -1 * (cos(it) + 1) * strength
            } else {
                0.0
            }
        }

        val center = Vector2(x = position, y = heightAt(position))

        val function2: (Vector3) -> Double = {
            val distance = abs(position - it.x)
            if (distance > strength) {
                it.y
            } else {
                it.y - sqrt(strength.pow(2) - distance.pow(2)) - abs((offset.y - it.z) * 0.005).pow(2)
            }
        }

        for (row in vertices) {
            for (vertex in row) {
                vertex.y = function2(vertex)
            }
        }
        updateMesh()
    }

    fun heightAt(position: Double): Double {
        val normPos = normalizePos(position)
        if (position !in vertices.first().first().y..vertices.last().first().y) return 0.0
        val previous = vertices[floor(normPos).toInt()].first()
        val next = vertices[ceil(normPos).toInt()].first()
        val progress = (position - previous.y) / pointDistance
        return getLinearGradient(previous.y, next.y, progress)
    }

    fun rotationAt(position: Double): Double {
        val normPos = normalizePos(position)
        if (normPos < 0 || normPos > width - 1) return 0.0
        val previous = floor(normPos).toInt()
        val next = ceil(normPos).toInt()
        val nearest = normPos.roundToInt()
        val currentAngle = angleFromAscent(averageAscent(previous, next))
        val nearestAsc = if (nearest == previous) {
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
    //endregion

    //region PRIVATE
    private fun normalizePos(position: Double): Double {
        val pos = position - offset.x.toFloat()
        return pos / pointDistance
    }

    private fun averageAscent(start: Int, end: Int): Double {
        return (vertices[end].first().y - vertices[start].first().y) / ((end - start) * pointDistance)
    }

    private fun updateMesh() {
        val positions = mutableListOf<Float>()
        val indices = mutableListOf<Int>()
        for (row in vertices) {
            for (vertex in row) {
                positions.addAll(vertex.toFloatList())
            }
        }
        for (i in 1..vertices.lastIndex) {
            for (j in 0 until vertices[i].lastIndex) {
                val frontStart: Int = i * vertices[i].size + j
                val backStart: Int = frontStart - vertices[i].size
                indices.addAll(
                    arrayOf(
                        frontStart, backStart, backStart + 1,
                        frontStart, backStart + 1, frontStart + 1,
                    )
                )
            }
        }
        geometry.setAttribute("position", BufferAttribute(Float32Array(positions.toTypedArray()).asDynamic(), 3))
        Log.v(TAG, "Indices length: ${indices.size}, data: $indices")
        geometry.setIndex(indices.toTypedArray())
        geometry.computeVertexNormals()
    }
    //endregion

    companion object {
        const val TAG = "Ground"
    }
}