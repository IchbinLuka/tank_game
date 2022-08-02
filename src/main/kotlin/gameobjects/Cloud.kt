package gameobjects

import core.GameObject
import three.GLTF
import three.js.Object3D
import util.Direction
import util.ResourceManager
import util.await

class Cloud(
    private val speed: Float = 1f,
    private val direction: Direction = Direction.Right
) : GameObject() {
    override val renderable: Object3D?
        get() = gltf?.scene

    private var gltf: GLTF? = null

    override suspend fun initVisual() {
        gltf = ResourceManager.cloud.await()
    }

    override fun update(deltaTime: Double) {
        gltf?.scene?.position?.addScaledVector(direction.vector3, speed * deltaTime)
    }
}