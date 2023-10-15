package gameobjects

import DevTools
import core.gameobjects.GameObject
import three.js.*
import util.backgroundGradient
import util.lightGradient
import kotlin.js.Date
import kotlin.math.PI
import kotlin.math.sin


class Sky(
    val period: Double = 10000.0,
    val scene: Scene? = null,
    val light: Light? = null
) : GameObject() {
    override val renderable: Object3D = Object3D().apply {
        add(
            Mesh(SphereGeometry(radius = 250, widthSegments = 30, heightSegments = 30), MeshBasicMaterial().apply {
                color = Color(0xFFBF00)
            }).apply {
                position.apply {
                    y = 800.0
                    z = -1500.0
                }
            }
        )
        add(
            Mesh(SphereGeometry(radius = 100, widthSegments = 30, heightSegments = 30), MeshBasicMaterial().apply {
                color = Color(0xFFFFFF)
            }).apply {
                position.apply {
                    y = -800.0
                    z = -1500.0
                }
            }
        )
    }

    override val initialChildren: Collection<GameObject> = listOf(
        Stars(period = period, radius = 1800.0),
    )

    override fun update(delta: Double) {
        super.update(delta)
        val time = Date.now() * DevTools.gameSpeed
        val rotation = (time * 2 * PI / period + PI / 2) % (2 * PI)
        renderable.rotation.z = rotation
        val progress = 0.5 + 0.5 * sin(time * 2 * PI / period)
        scene?.background = backgroundGradient.getColor(progress)
        light?.color = lightGradient.getColor(progress)
    }
}