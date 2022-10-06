package gameobjects

import core.HeightMap
import core.gameobjects.GameObject
import ext.minus
import ext.toFloatArray
import ext.toFloatList
import org.khronos.webgl.Float32Array
import tests.FlatNoise
import three.DoubleSide
import three.js.*
import util.Log
import util.noise.GroundNoise

class Ground(
    private val width: Int = 200,
    private val offset: Vector3 = Vector3(0, 0, 0),
    private val height: Int = 40,
    private val depth: Int = 20
) : GameObject() {
    override val renderable: Object3D by lazy {
        Group().apply {
            add(Mesh(geometry, MeshPhongMaterial().apply {
                color = Color(0x195917)
                side = DoubleSide
                shininess = 30
                flatShading = true
            }))
            add(Points(geometry, PointsMaterial().apply {
                color = Color(0xffffff)
                size = 0.25
            }))
        }
    }

    /*get() {
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

    }*/
    private val geometry = BufferGeometry()

    // private val heightData: Array<Double> = Array(res) { (height * 0.2 * (1 - sin(0.5 * it))) + height / 2 }
    private val data: HeightMap = FlatNoise(width, depth).allValues() // GroundNoise(width = width, depth = depth).allValues()
    private var vertices = Array<Vector3>(data.size * data.first().size) {
        val pointDistance = 1
        val heightScale = 1
        val j = it % data.size
        val i = it / data.size
        Vector3(
            i * pointDistance,
            data[j][i].toFloat() * heightScale,
            j * pointDistance
        )
    }

    override suspend fun initVisual() {
        updateMesh()
    }

    //region PUBLIC
    fun heightAt(position: Double): Double {
        TODO()
        /*val pos = position - offset.x.toFloat()
        if (pos < 0 || pos > width - 1) return 0.0
        val normPos = pos * res / width
        val previous = heightData[floor(normPos).toInt()]
        val next = heightData[ceil(normPos).toInt()]
        val progress = 1 - ceil(normPos) + normPos
        return offset.y.toFloat() + getLinearGradient(previous, next, progress)*/
    }

    fun rotationAt(position: Double): Double {
        TODO()
        /*val pos = position - offset.x.toFloat()
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
        return getLinearGradient(angleFromAscent(nearestAsc), currentAngle, progress)*/
    }
    //endregion

    //region PRIVATE
    private fun averageAscent(start: Int, end: Int): Double {
        TODO()
        // return (heightData[end] - heightData[start]) / (end - start)
    }

    private fun updateMesh() {
        val positions = mutableListOf<Float>()
        val normals = mutableListOf<Float>()
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
                val v = vertices[frontStart] - vertices[backStart]
                val u = vertices[backStart] - vertices[backStart + 1]
                val normal = v.cross(u).toFloatArray()
                repeat(2) {
                    normals.addAll(normal)
                }

            }
        }
        geometry.setAttribute("position", BufferAttribute(Float32Array(positions.toTypedArray()).asDynamic(), 3))
        Log.v(TAG, "Indices length: ${indices.size}, data: $indices")
        geometry.setIndex(indices.toTypedArray())
        // geometry.setAttribute("normal", BufferAttribute(Float32Array(normals.toTypedArray()).asDynamic(), 3))
        geometry.computeVertexNormals()
    }
    //endregion

    companion object {
        const val TAG = "Ground"
    }
}