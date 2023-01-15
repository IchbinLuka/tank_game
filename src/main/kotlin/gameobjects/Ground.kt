package gameobjects

import core.HeightMap
import core.gameobjects.GameObject
import ext.minus
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
import kotlin.time.ExperimentalTime

class Ground(
    private val offset: Vector3 = Vector3(0, 0, 0),
    showVertex: Boolean = false,
    private val heightScale: Double = 1.0,
    private val pointDistance: Double = 1.0,
    data: HeightMap = GroundNoise(width = 150, depth = 20, min = -40.0).allValues()
) : GameObject() {

    override val renderable: Object3D by lazy {
        Group().apply {
            add(Mesh(geometry, MeshPhongMaterial().apply {
                color = Color(0x156812)
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
    private val positionAttribute: BufferAttribute
        get() = geometry.getAttribute("position") as BufferAttribute

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

    private fun getIndex(x: Int, y: Int): Int = x * vertices.first().size + y

    //region PUBLIC
    @OptIn(ExperimentalTime::class)
    fun destroy(position: Double, strength: Double) {
        // TODO
        val function: (Vector3) -> Double = {
            it.y - 20
        }
        val maxDistance = strength / pointDistance
        val height = heightAt(position) ?: return
        val function2: (Vector3) -> Double = {
            val distance = abs(position - it.x)
            if (distance > maxDistance) {
                it.y
            } else {
                min(height - sqrt(maxDistance.pow(2) - distance.pow(2)), it.y) //- abs((offset.y - it.z) * 0.0005).pow(2)
            }
        }
        val attribute = positionAttribute
        val nearestVertex = floor(normalizePos(position)).toInt()
        val range = max(
            0.0, nearestVertex - maxDistance
        ).toInt()..min(
            vertices.size.toDouble(),
            nearestVertex + maxDistance
        ).toInt()
        for (i in range) {
            for (j in 0 until vertices[i].size) {
                val index = getIndex(i, j)
                attribute.setY(index, function2(
                    Vector3(
                        attribute.getX(index),
                        attribute.getY(index),
                        attribute.getZ(index)
                    )
                ))
            }
        }
        attribute.needsUpdate = true
        geometry.setAttribute("position", attribute)
        geometry.computeVertexNormals()
    }

    fun heightAt(position: Double): Double? {
        val normPos = normalizePos(position)
        if (position !in vertices.first().first().x..vertices.last().first().x) {
            Log.d(TAG, "Position $position not in range")
            return null
        }
        val attribute = positionAttribute
        val previousIndex = getIndex(floor(normPos).toInt(), 0)
        val nextIndex = getIndex(ceil(normPos).toInt(), 0)

        val progress = (position - attribute.getX(previousIndex).toDouble()) / pointDistance
        return getLinearGradient(attribute.getY(previousIndex).toDouble(), attribute.getY(nextIndex).toDouble(), progress)
    }

    fun rotationAt(position: Double): Double {
        val normPos = normalizePos(position)
        if (normPos < 0 || normPos > vertices.size - 1) return 0.0
        val previous = floor(normPos).toInt()
        val next = floor(normPos + 1).toInt()
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
        return (positionAttribute.getY(getIndex(end, 0)) - positionAttribute.getY(getIndex(start, 0))).toDouble() / ((end - start) * pointDistance)
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
        geometry.setIndex(indices.toTypedArray())
        geometry.computeVertexNormals()
    }
    //endregion

    companion object {
        const val TAG = "Ground"
    }
}