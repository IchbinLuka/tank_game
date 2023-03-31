package gameobjects

import core.gameobjects.GameObject
import ext.set
import three.js.*
import kotlin.random.Random

class Background(
    private val offset: Vector3 = Vector3()
) : GameObject() {
    override val renderable: Object3D = Scene().apply {
        position.set(offset)
    }

    private val cloudSpeed = 1f
    private val random = Random(1)

    private val clouds = Array<Cloud>(20) {
        Cloud(
            speed = cloudSpeed,
            min = -400.0,
            max = 400.0,
            startPos = Vector3(
                x = 800 * random.nextDouble() - 400,
                y = 45 * random.nextDouble() + 130,
                z = 30 * random.nextDouble() - 100
            )
        )
    }

    override val _children: MutableMap<String, GameObject> = hashMapOf<String, GameObject>().apply {
        putAll(clouds.map { it.uuid.toString() to it })
    }

    override suspend fun preloadAssets() {
        super.preloadAssets()
        val sun = Mesh(SphereGeometry(radius = 50, widthSegments = 30, heightSegments = 30), MeshBasicMaterial().apply {
            color = Color(0xFFBF00)
        }).apply {
            position.apply {
                x = 50.0
                y = 250.0
                z = -200.0
            }
        }
        renderable.add(sun)
    }

    override fun update(delta: Double) {
        super.update(delta)
        for (cloud in clouds) cloud.update(delta)
    }
}