package gameobjects

import core.GameObject
import ext.enableShadows
import ext.getWorldEuler
import ext.minus
import three.GLTF
import three.js.*
import util.*
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.atan

class Tank() : GameObject() {
    override val renderable: Object3D? get() = gltf?.scene

    private var gltf: GLTF? = null
    private var mainBone: Object3D? = null

    private var direction = Direction.Right

    private var cannon: Object3D? = null

    override suspend fun initVisual() {
        Log.v("Loading tank")
        gltf = ResourceManager.tank.await()
        gltf?.scene?.enableShadows()
        mainBone = gltf?.scene?.children?.firstOrNull { it.name == "Main" }
        mainBone?.position?.y = 0
        cannon = mainBone?.children?.firstOrNull { it.name == "Cannon" }
        Log.d("Loaded tank")
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

    fun move(vector3: Vector3, deltaTime: Float = 1f, ground: Ground) {
        mainBone?.position?.addScaledVector(vector3, deltaTime)
        mainBone?.position?.let {
            val pos = mainBone!!.position.x.toFloat()
            it.y = ground.heightAt(pos)
            val rotation = ground.rotationAt(pos)
            mainBone?.rotation?.z = if (direction == Direction.Right) rotation else (2 * PI - rotation).toFloat()
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

    override fun update(deltaTime: Float) {
    }


}