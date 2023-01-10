package gameobjects

import core.gameobjects.GameObject
import ext.applyCustomMat
import ext.set
import three.js.Color
import three.js.MeshToonMaterial
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
    override var renderable: Object3D? = null

    override suspend fun initVisual() {
        renderable = ResourceManager.cloud.await().scene.clone(true).apply {
            applyCustomMat(MeshToonMaterial().apply {
                color = Color(0xffffff)
            })
        }
        renderable!!.position.set(startPos)
    }

    override fun update(deltaTime: Double) {
        renderable?.position?.addScaledVector(startDirection.vector3, speed * deltaTime)
        if (position.x > max) {
            position.x = min
        } else if (position.x < min) {
            position.x = max
        }
    }

    companion object {

        suspend fun initAssets() {
            ResourceManager.cloud.await()
        }

    }
}