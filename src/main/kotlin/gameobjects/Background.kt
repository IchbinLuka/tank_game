package gameobjects

import core.gameobjects.GameObject
import ext.set
import three.js.Object3D
import three.js.Scene
import three.js.Vector3
import kotlin.random.Random

const val DAY_PERIOD = 400000.0

class Background(
    private val offset: Vector3 = Vector3(),
    override val name: String = "background",
    val scene: Scene? = null
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
                y = 65 * random.nextDouble() + 140,
                z = 30 * random.nextDouble() - 100
            )
        )
    }

    override val initialChildren: Collection<GameObject> = listOf(
        Sky(scene = scene, period = DAY_PERIOD),
        *clouds,
    )

    override fun update(delta: Double) {
        super.update(delta)
        for (cloud in clouds) cloud.update(delta)
    }
}