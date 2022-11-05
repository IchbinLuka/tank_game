package gameobjects

import core.gameobjects.GameObject
import ext.set
import three.GLTF
import three.js.Object3D
import three.js.Vector3
import util.Direction
import util.ResourceManager

class Cloud(
    private val speed: Float = 1f,
    private val startDirection: Direction = Direction.Right,
    private val min: Double,
    private val max: Double,
    private val startPos: Vector3
) : GameObject() {
    override val renderable: Object3D?
        get() = gltf?.scene

    private var gltf: GLTF? = null

    override suspend fun initVisual() {
        gltf = ResourceManager.cloud.await()
        gltf?.scene?.position?.set(startPos)
    }

    override fun update(deltaTime: Double) {
        gltf?.scene?.position?.addScaledVector(startDirection.vector3, speed * deltaTime)
    }
}