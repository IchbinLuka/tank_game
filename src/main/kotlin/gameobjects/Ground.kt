package gameobjects

import GameMessage
import GameMessageData
import core.*
import core.gameobjects.GameObject
import core.multiplayer.MessageBroadcaster
import core.multiplayer.MultiplayerSynchronized
import ext.*
import gameobjects.bullets.BulletObject
import gameobjects.bullets.BulletTarget
import noise.GroundNoise
import org.khronos.webgl.Float32Array
import sessions.Topic
import three.DoubleSide
import three.js.*
import util.Log
import util.angleFromAscent
import util.getLinearGradient
import kotlin.math.*

private const val BACK_SMOOTHING_WIDTH = 3

class Ground(
    private val offset: Vector3 = Vector3(0, 0, 0),
    showVertex: Boolean = false,
    private val heightScale: Double = 1.0,
    private val pointDistance: Double = 1.0,
    override val name: String = "ground",
    data: HeightMap = GroundNoise(width = 150, depth = 20, min = -40.0).allValues()
) : GameObject(), BulletTarget, Hittable, MultiplayerSynchronized {

    override val topic: String = Topic.GROUND

    // region - HitBox -

    override val hitBox: HitBox = object : HitBox() {
        override val associatedObject: Hittable = this@Ground
        override val vertices: Iterable<Vector3> = emptyList()

        override fun containsPoint(point: Vector3): Boolean =
            heightAt(point.x)?.let { it > point.y } ?: false

    }

    private val broadcaster: MessageBroadcaster = ConnectionManager

    override val initialChildren: Collection<GameObject> = listOf(hitBox)

    override fun onBulletImpact(bullet: BulletObject) {
        hitBox.onIntersectionEnter(bullet)
    }

    // endregion

    override val renderable: Object3D by lazy {
        Group().apply {
            add(Mesh(geometry, MeshPhongMaterial().apply {
                // color = Color(0x156812)
                side = DoubleSide
                shininess = 30
                flatShading = true
                vertexColors = true
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
        get() = geometry.getAttribute("position")

    private var initialPositions: BufferAttribute? = null

    private val colorAttribute: BufferAttribute
        get() = geometry.getAttribute("color")

    private var groundVertices = Array(data.size) { i ->
        Array<Vector3>(data[i].size) { j ->
            Vector3(
                i * pointDistance,
                data[i][j] * heightScale,
                j * pointDistance
            ) + offset
        }
    }


    override suspend fun preloadAssets() {
        super.preloadAssets()
        updateMesh()
    }

    private fun getIndex(x: Int, y: Int): Int = x * (groundVertices.first().size + BACK_SMOOTHING_WIDTH) + y

    init {
        ConnectionManager.register(this)
    }

    //region PUBLIC
    fun destroy(position: Double, strength: Double, broadcast: Boolean = true) {
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
                )
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
            for (j in BACK_SMOOTHING_WIDTH until groundVertices[i].size) {
                val index = getIndex(i, j)
                val newY = function2(
                    Vector3(
                        attribute.getX(index),
                        attribute.getY(index),
                        attribute.getZ(index)
                    )
                )
                if (newY != attribute.getY(index)) {
                    val color = Color(
                        if (initialPositions!!.getY(index).toDouble() - newY > 15) {
                            ROCK_COLOR
                        } else {
                            DIRT_COLOR
                        }
                    )
                    colorAttribute.setXYZ(index, color.r, color.g, color.b)
                }
                attribute.setY(
                    index, newY
                )
            }
            val newHeight = attribute.getY(getIndex(i, BACK_SMOOTHING_WIDTH))
            for (j in 0 until BACK_SMOOTHING_WIDTH) {
                val index = getIndex(i, j)
                attribute.setY(index, newHeight - (BACK_SMOOTHING_WIDTH - j).toDouble().pow(2))
            }
        }
        colorAttribute.needsUpdate = true
        attribute.needsUpdate = true
        geometry.setAttribute("position", attribute)
        geometry.computeVertexNormals()
        if (broadcast) {
            broadcaster.broadcast(
                GameMessage.fromGameMessageData(
                    GameMessageData.GroundDestruction(
                        pos = position,
                        strength = strength
                    ),
                    Topic.GROUND
                )
            )
        }
        for (tank in currentScene.findObjects<Tank>()) {
            tank.adjustTransformToGround(this)
        }
    }

    fun heightAt(position: Double): Double? {
        val normPos = normalizePos(position)
        if (position !in groundVertices.first().first().x..groundVertices.last().first().x) {
            Log.d(TAG, "Position $position not in range")
            return null
        }
        val attribute = positionAttribute
        val previousIndex = getIndex(floor(normPos).toInt(), BACK_SMOOTHING_WIDTH)
        val nextIndex = getIndex(ceil(normPos).toInt(), BACK_SMOOTHING_WIDTH)

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

    fun ascendAt(position: Double): Double {
        val normPos = normalizePos(position)
        if (normPos < 0 || normPos > groundVertices.size - 1) return 0.0
        val previousIndex = getIndex(floor(normPos).toInt(), 0)
        val nextIndex = getIndex(floor(normPos + 1).toInt(), 0)

        return averageAscent(previousIndex, nextIndex)
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
        val groundVertices = groundVertices.map {
            Array(size = BACK_SMOOTHING_WIDTH) { i ->
                val j = BACK_SMOOTHING_WIDTH - i
                it.first() - Vector3(y = j.toDouble().pow(2), z = pointDistance * j)
            }.asSequence().plus(it).toList()
            // sequenceOf(it.first() - Vector3(y = 1, z = pointDistance))
        }


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

        val colors = sequenceOf(Color(0x238e1f)).repeat().take(positions.size).flatMap { it.toFloatList() }.toList()

        geometry.setAttribute("position", BufferAttribute(Float32Array(positions.toTypedArray()).asDynamic(), 3))
        geometry.setAttribute("color", BufferAttribute(Float32Array(colors.toTypedArray()).asDynamic(), 3))
        geometry.setIndex(indices.toTypedArray())
        geometry.computeVertexNormals()
        initialPositions = positionAttribute.clone()
    }
    //endregion

    fun getIndices(x: Int, y: Int): Array<Int> {
        val frontStart: Int = x * groundVertices[x].size + y
        val backStart: Int = frontStart - groundVertices[y].size
        return arrayOf(
            frontStart, backStart, backStart + 1,
            frontStart, backStart + 1, frontStart + 1,
        )
    }

    companion object {
        const val TAG = "Ground"
        const val GRASS_COLOR = 0x238e1f
        const val DIRT_COLOR = 0x8b4513
        const val ROCK_COLOR = 0x808080
    }

    override fun onMessage(message: GameMessageData) {
        when (message) {
            is GameMessageData.GroundDestruction -> {
                destroy(message.pos, message.strength, false)
            }
            else -> {}
        }
    }
}