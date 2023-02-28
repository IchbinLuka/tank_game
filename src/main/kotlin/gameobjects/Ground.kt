package gameobjects

import core.HeightMap
import core.HitBox
import core.Hittable
import core.gameobjects.GameObject
import ext.TAG
import ext.minus
import ext.plus
import ext.toFloatList
import gameobjects.bullets.BulletObject
import org.khronos.webgl.Float32Array
import three.DoubleSide
import three.js.*
import util.Log
import util.angleFromAscent
import util.getLinearGradient
import util.noise.GroundNoise
import kotlin.math.*

class Ground(
    private val offset: Vector3 = Vector3(0, 0, 0),
    showVertex: Boolean = false,
    private val heightScale: Double = 1.0,
    private val pointDistance: Double = 1.0,
    data: HeightMap = GroundNoise(width = 150, depth = 20, min = -40.0).allValues()
) : GameObject(), Hittable {

    // region - HitBox -

    override val hitBox: HitBox = object : HitBox() {
        override val associatedObject: Hittable = this@Ground
        override val vertices: Iterable<Vector3> = emptyList()
        override fun containsPoint(point: Vector3): Boolean =
            heightAt(point.x)?.let { it > point.y } ?: false

        override fun onSceneReady() {
            super.onSceneReady()
            // TODO destroy ground when tank is hit
            /*Log.d(TAG, "Adding listeners")
            for (tank in currentScene.findObjects<Tank>()) {
                tank.hitBox.addIntersectionListener(::onIntersectionEnter)
            }*/
        }

        override fun onIntersectionEnter(intersectingBox: Hittable) {
            super.onIntersectionEnter(intersectingBox)

            Log.d(TAG, "intersected with ${intersectingBox.TAG}")
            if (intersectingBox is BulletObject) {
                destroy(intersectingBox.position.x, intersectingBox.explosionWidth)
            }
        }
    }

    override val _children: MutableMap<String, GameObject> = hashMapOf("hitBox" to hitBox)

    // endregion

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
    private var groundVertices = Array<Array<Vector3>>(data.size) { i ->
        Array<Vector3>(data[i].size) { j ->
            Vector3(
                i * pointDistance,
                data[i][j] * heightScale,
                j * pointDistance
            ) + offset
        }
    }

    override suspend fun initVisual() {
        super.initVisual()
        updateMesh()
    }

    private fun getIndex(x: Int, y: Int): Int = x * groundVertices.first().size + y

    //region PUBLIC
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
                min(
                    height - sqrt(maxDistance.pow(2) - distance.pow(2)),
                    it.y
                ) //- abs((offset.y - it.z) * 0.0005).pow(2)
            }
        }
        val attribute = positionAttribute
        val nearestVertex = floor(normalizePos(position)).toInt()
        val range = max(
            0.0, nearestVertex - maxDistance
        ).toInt()..min(
            groundVertices.size.toDouble(),
            nearestVertex + maxDistance
        ).toInt()
        for (i in range) {
            for (j in 0 until groundVertices[i].size) {
                val index = getIndex(i, j)
                attribute.setY(
                    index, function2(
                        Vector3(
                            attribute.getX(index),
                            attribute.getY(index),
                            attribute.getZ(index)
                        )
                    )
                )
            }
        }
        attribute.needsUpdate = true
        geometry.setAttribute("position", attribute)
        geometry.computeVertexNormals()
    }

    fun heightAt(position: Double): Double? {
        val normPos = normalizePos(position)
        if (position !in groundVertices.first().first().x..groundVertices.last().first().x) {
            Log.d(TAG, "Position $position not in range")
            return null
        }
        val attribute = positionAttribute
        val previousIndex = getIndex(floor(normPos).toInt(), 0)
        val nextIndex = getIndex(ceil(normPos).toInt(), 0)

        val progress = (position - attribute.getX(previousIndex).toDouble()) / pointDistance
        return getLinearGradient(
            attribute.getY(previousIndex).toDouble(),
            attribute.getY(nextIndex).toDouble(),
            progress
        )
    }

    fun rotationAt(position: Double): Double {
        val normPos = normalizePos(position)
        if (normPos < 0 || normPos > groundVertices.size - 1) return 0.0
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
        val pos = position - offset.x
        return pos / pointDistance
    }

    private fun averageAscent(start: Int, end: Int): Double {
        return (positionAttribute.getY(getIndex(end, 1)) - positionAttribute.getY(
            getIndex(
                start,
                1
            )
        )).toDouble() / ((end - start) * pointDistance)
    }

    private fun updateMesh() {
        val positions = mutableListOf<Float>()
        val indices = mutableListOf<Int>()
        /*val groundVertices = groundVertices.map {
            arrayOf(it.first() - Vector3(y = 1, z = pointDistance)).asSequence().plus(it).toList()
        }*/

        for (row in groundVertices) {
            for (vertex in row) {
                positions.addAll(vertex.toFloatList())
            }
        }
        for (i in 1..groundVertices.lastIndex) {
            for (j in 0 until groundVertices[i].lastIndex) {
                val frontStart: Int = i * groundVertices[i].size + j
                val backStart: Int = frontStart - groundVertices[i].size
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