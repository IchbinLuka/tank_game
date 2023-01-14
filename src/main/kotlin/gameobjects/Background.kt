package gameobjects

import core.gameobjects.GameObject
import three.js.*
import util.Direction
import kotlin.random.Random

class Background : GameObject() {
    override val renderable: Object3D = Scene()

    private val cloudSpeed = 1f
    private val random = Random(1)

    private val clouds = Array<Cloud>(20) {
        Cloud(
            speed = cloudSpeed,
            startDirection = Direction.Left,
            min = -300.0,
            max = 300.0,
            startPos = Vector3(
                x = 400 * random.nextDouble() - 200,
                y = 45 * random.nextDouble() + 130,
                z = 30 * random.nextDouble() - 100
            )
        )
    }

    override suspend fun initVisual() {
        super.initVisual()
        val sun = Mesh(SphereGeometry(radius = 50, widthSegments = 30, heightSegments = 30), MeshBasicMaterial().apply {
            color = Color(0xFFBF00)
        }).apply {
            position.apply {
                x = 50.0
                y = 250.0
                z = -200.0
            }
        }
        for (cloud in clouds) {
            cloud.initVisual()
            cloud.renderable?.let { renderable.add(it) }
        }
        renderable.add(sun)
    }

    override fun update(deltaTime: Double) {
        super.update(deltaTime)
        for (cloud in clouds) cloud.update(deltaTime)
    }
}