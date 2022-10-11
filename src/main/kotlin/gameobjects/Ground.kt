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
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

class Ground(
    val width: Int = 200,
    private val offset: Vector3 = Vector3(0, 0, 0),
    height: Double = 40.0,
    depth: Int = 20,
    showVertex: Boolean = false,
    private val heightScale: Double = 3.0,
    private val pointDistance: Double = 1.0
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
    private val data: HeightMap =
        GroundNoise(width = width, depth = depth, min = -height).allValues()
    private var vertices: List<Vector3> = Array<Array<Vector3>>(data.size) { i ->
        Array<Vector3>(data[i].size) { j ->
            Vector3(
                i * pointDistance,
                data[i][j].toFloat() * heightScale,
                j * pointDistance
            ) + offset
        }
    }.flatten()

    override suspend fun initVisual() {
        updateMesh()
    }

    //region PUBLIC
    fun heightAt(position: Double): Double {
        val pos = position - offset.x.toFloat()
        val normPos = pos / pointDistance
        if (normPos < 0 || normPos > width - 1) return 0.0
        val previous = data[floor(normPos).toInt()].first() * heightScale
        val next = data[ceil(normPos).toInt()].first() * heightScale
        val progress = 1 - ceil(normPos) + normPos
        return offset.y.toFloat() + getLinearGradient(previous, next, progress)
    }

    fun rotationAt(position: Double): Double {
        val pos = position - offset.x.toFloat()
        val normPos = pos / pointDistance
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
    private fun averageAscent(start: Int, end: Int): Double {
        return (data[end].first() - data[start].first()) * heightScale / ((end - start) * pointDistance)
    }

    private fun updateMesh() {
        val positions = mutableListOf<Float>()
        val indices = mutableListOf<Int>()
        for (vertex in vertices) {
            positions.addAll(vertex.toFloatList())
        }
        Log.d(TAG, "Size: ${data.size} x ${data.first().size}")
        for (i in 1..data.lastIndex) {
            for (j in 0 until data[i].lastIndex) {
                val frontStart: Int = i * data[i].size + j
                val backStart: Int = frontStart - data[i].size
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