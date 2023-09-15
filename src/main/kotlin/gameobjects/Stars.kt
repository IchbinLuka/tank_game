package gameobjects

import core.gameobjects.GameObject
import ext.toFloatList
import gameobjects.particles.emitter.SphereEmitter
import org.khronos.webgl.Float32Array
import three.js.*
import util.ResourceManager
import kotlin.js.Date
import kotlin.math.PI
import kotlin.math.sin

class Stars(
    private val radius: Double = 1000.0,
    private val numStars: Int = 1000,
    private val period: Double = 1000.0,
    override val name: String = "stars"
) : GameObject() {

    private val geometry = BufferGeometry()

    private lateinit var material: PointsMaterial

    override var renderable: Object3D? = null

    override suspend fun preloadAssets() {
        super.preloadAssets()

        material = PointsMaterial().apply {
            size = 20.0
            color = Color(0xffffff)
            transparent = true
            alphaMap = ResourceManager.disc.await()
        }

        val sphereEmitter = SphereEmitter(
            position = Vector3(),
            radius = radius,
            seed = 1
        )
        val vertices: Array<Float> = Array<Vector3>(numStars) {
            sphereEmitter.randomPoint().first.vector3
        }.flatMap { it.toFloatList() }.toTypedArray()

        geometry.setAttribute("position", BufferAttribute(Float32Array(vertices).asDynamic(), 3))

        renderable = Points(geometry, material)
    }

    override fun update(delta: Double) {
        super.update(delta)
        val time = Date.now()
        material.opacity = 0.5 + 0.5 * sin(time * 2 * PI / period)
    }
}