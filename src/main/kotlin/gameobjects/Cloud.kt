package gameobjects

import core.gameobjects.GameObject
import ext.applyCustomMat
import ext.set
import three.js.Color
import three.js.MeshToonMaterial
import three.js.Object3D
import three.js.Vector3
import util.ResourceManager

class Cloud(
    private val speed: Float = 1f,
    private val min: Double,
    private val max: Double,
    private val startPos: Vector3
) : GameObject() {
    override var renderable: Object3D? = null

    private var offset = startPos.x


    override suspend fun preloadAssets() {
        super.preloadAssets()
        renderable = ResourceManager.cloud.await().scene.clone(true).apply {
            applyCustomMat(MeshToonMaterial().apply {
                color = Color(0xffffff)
            })
        }
        renderable!!.position.set(startPos)
    }

    override fun update(delta: Double) {
        offset += speed * delta
        position.x = ((offset - min) % (max - min)) + min
    }

    companion object {

        suspend fun initAssets() {
            ResourceManager.cloud.await()
        }

    }
}