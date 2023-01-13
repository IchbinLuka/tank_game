package gameobjects

import core.gameobjects.GameObject
import ext.*
import three.GLTF
import three.js.*
import util.Direction
import util.Log
import util.ResourceManager
import util.toUnitCircle
import kotlin.math.*

class Tank(
    val color: Color = Color(0x338530),
    val startPosition: Vector3,
    val name: String = "Unknown"
) : GameObject() {
    override val renderable: Object3D? get() = gltf?.scene

    override var position: Vector3
        get() = mainBone?.position!!
        set(value) {
            mainBone?.position?.set(value)
        }

    private var gltf: GLTF? = null
    private var mainBone: Object3D? = null

    private var direction = Direction.Right

    private var cannon: Object3D? = null

    val health: Double
        get() = _health
    private var _health: Double = 1.0

    val active: Boolean
        get() = health > 0

    override suspend fun initVisual() {
        Log.v(TAG, "Loading tank")
        gltf = ResourceManager.tank.await()
        gltf?.scene?.apply {
            enableShadows()
            applyCustomMat(MeshToonMaterial().apply {
                color = this@Tank.color
            })
        }
        mainBone = gltf?.scene?.children?.firstOrNull { it.name == "Main" }
        mainBone?.position?.set(startPosition)
        cannon = mainBone?.children?.firstOrNull { it.name == "Cannon" }
        if (mainBone == null || cannon == null) {
            Log.e(TAG, "Failed to identify mainBone and cannon")
            alive = false
        }
        Log.d(TAG, "Loaded tank")
    }

    fun aim(point: Vector2) {
        if (cannon?.rotation == null) return
        //Log.v("Position: ${cannon!!.position.toFloatList()}")
        val globalpos = cannon!!.getWorldPosition(Vector3())
        val delta = Vector2(point.x - globalpos.x, point.y - globalpos.y)
        var rotation = atan(delta.y.toFloat().absoluteValue / delta.x.toFloat().absoluteValue)
        val globalRot = cannon!!.getWorldEuler(Euler())
        if (delta.x.toFloat() * delta.y.toFloat() * direction.vector3.x.toFloat() < 0) {
            rotation *= -1
        }
        if (delta.x.toFloat() < 0) {
            rotation += PI.toFloat()
        }
        val angle = rotation - globalRot.z - 0.5 * PI
        if (toUnitCircle(cannon!!.rotation.z.toDouble() + angle.toDouble()) < 2.8) {
            cannon?.rotateOnWorldAxis(Vector3(0, 0, 1), angle)
        }
        if (cannon!!.rotation.z.toFloat() > 2.3f) {
            switchDirection()
            // TODO: Aim to correct spot after turning
        }
    }

    fun shoot(velocity: Double, ground: Ground, onImpact: (Vector3) -> Unit): Bullet? {
        if (cannon == null) return null
        val worldPosition = cannon!!.getWorldPosition(Vector3())
        val worldRotation = cannon!!.getWorldEuler(Euler())
        Log.v(TAG, worldRotation.toVector3().toFloatList().toString())
        return Bullet(
            startPos = Vector3(worldPosition.x, worldPosition.y),
            startVelocity = Vector3(
                -sin(worldRotation.z.toDouble()) * velocity,
                cos(worldRotation.z.toDouble()) * velocity * direction.vector3.x.toDouble(),
            ),
            ground = ground,
            onImpact = onImpact
        )
    }

    fun move(vector3: Vector3, deltaTime: Double = 1.0, ground: Ground) {
        mainBone?.position?.let {
            val ascend = abs(ground.rotationAt(it.x))
            if (ascend > 0.5 * PI) {
                it.addScaledVector(vector3, deltaTime)
            }
        }
        adjustTransformToGround(ground)
        Log.d(TAG, "Position: ${mainBone?.position?.toFloatList()}")
    }

    fun adjustTransformToGround(ground: Ground) {
        mainBone?.position?.let {
            val pos = mainBone!!.position.x
            it.y = ground.heightAt(pos) ?: 0.0
            val rotation = ground.rotationAt(pos)
            mainBone?.rotation?.z = if (direction == Direction.Right) rotation else (2 * PI - rotation).toFloat()
        }
    }

    fun damage(amount: Double) {
        _health = max(_health - amount, 0.0)
        if (_health == 0.0) {
            // TODO: Show fire particles
        }
    }

    private fun setDirection(direction: Direction) {
        if (direction != this.direction) {
            mainBone?.rotateX(PI)
        }
        this.direction = direction
    }

    private fun switchDirection() {
        setDirection(direction.inverse)
    }

    override fun update(deltaTime: Double) {
    }
}