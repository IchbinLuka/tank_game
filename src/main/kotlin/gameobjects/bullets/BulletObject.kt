package gameobjects.bullets

import core.Game
import core.gameobjects.MovingGameObject
import ext.set
import ext.toFloatList
import gameobjects.Ground
import kotlinx.coroutines.asPromise
import org.khronos.webgl.Float32Array
import three.js.*
import util.AssetInitializer
import util.ResourceManager

typealias ImpactListener = (Vector3) -> Unit

open class BulletObject(
    startVelocity: Vector3,
    startPos: Vector3,
) : MovingGameObject() {

    private val ground: Ground

    private val impactListeners: MutableList<ImpactListener> = mutableListOf()

    fun addImpactListener(listener: ImpactListener) {
        impactListeners.add(listener)
    }

    fun removeImpactListener(listener: ImpactListener) {
        impactListeners.remove(listener)
    }

    open val gravity = Vector3(0, -20, 0)

    val explosionWidth: Double = 30.0
    val damage: Double = 0.5

    override val renderable: Object3D by lazy {
        Group().apply {
            add(bulletMesh)
        }
    }

    override var position: Vector3
        get() = bulletMesh.position
        set(value) {
            bulletMesh.position.set(value)
        }

    private val dots = BufferGeometry()
    private val vertices = ArrayDeque<Float>()
    private var dotTimer: Double = 0.0

    private val bulletMesh = Mesh(
        SphereGeometry(radius = bulletRadius),
        MeshPhongMaterial().apply {
            color = Color(0x111111)
        }
    ).apply { this@apply.position.set(startPos) }

    init {
        ground = Game.instance.currentScene.findObject<Ground>()!!
        ResourceManager.disc.asPromise().then {
            renderable.add(Points(dots,
                PointsMaterial().apply {
                    color = Color(0xffffff)
                    size = 3
                    alphaMap = it
                    map = it
                    alphaTest = 0.5
                }
            ))
        }
    }

    override suspend fun initVisual() {}

    override var velocity: Vector3 = startVelocity
    override var acceleration: Vector3
        get() = gravity
        set(value) {}

    private fun updateGeometry() {
        dots.setAttribute("position", BufferAttribute(Float32Array(vertices.toTypedArray()).asDynamic(), 3))
    }

    override fun update(deltaTime: Double) {
        super.update(deltaTime)
        dotTimer += deltaTime
        if (dotTimer > 0.5) {
            dotTimer = 0.0
            vertices.apply {
                position.toFloatList().reversed().forEach {
                    addFirst(it)
                }
            }
            updateGeometry()
        }
        if (vertices.size > 3 * 10) {
            for (i in 1..3) {
                vertices.removeLast()
            }
            updateGeometry()
        }
        val height = ground.heightAt(position.x)
        if (height == null || height > position.y.toFloat()) {
            alive = false
            ground.destroy(position.x, explosionWidth)
            val pos = position.clone().apply { y = height ?: y }
            for (listener in impactListeners) {
                listener(pos)
            }
        }
    }

    companion object: AssetInitializer {
        const val bulletRadius = 0.5f

        override suspend fun initAssets() {
            ResourceManager.disc.await()
        }
    }
}