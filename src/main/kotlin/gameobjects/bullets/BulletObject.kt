package gameobjects.bullets

import core.Game
import core.HitBox
import core.Hittable
import core.currentScene
import core.gameobjects.GameObject
import core.gameobjects.MovingGameObject
import ext.TAG
import ext.set
import ext.times
import ext.toFloatList
import gameobjects.Ground
import gameobjects.Tank
import gameobjects.particles.Explosion
import kotlinx.coroutines.asPromise
import org.khronos.webgl.Float32Array
import three.js.*
import util.AssetInitializer
import util.Log
import util.ResourceManager
import kotlin.math.max
import kotlin.math.pow

typealias ImpactListener = (Vector3) -> Unit

open class BulletObject(
    startVelocity: Vector3,
    protected val tank: Tank,
) : MovingGameObject(), Hittable {

    // region - HitBox -

    final override val hitBox: HitBox = object: HitBox() {
        override val associatedObject: Hittable
            get() = this@BulletObject

        override val vertices: Iterable<Vector3>
            get() = listOf(this@BulletObject.position)

        override fun containsPoint(point: Vector3): Boolean = false

        override fun onIntersectionEnter(intersectingBox: Hittable) {
            super.onIntersectionEnter(intersectingBox)
            Log.d(TAG, "intersected with ${intersectingBox.TAG}")
            if (isNew) return
            this@BulletObject.onIntersectionEnter(intersectingBox)
        }
    }

    override val _children: MutableMap<String, GameObject> = hashMapOf("hitBox" to hitBox)

    // endregion

    protected val ground: Ground = Game.instance.currentScene.findObject<Ground>()!!

    private val impactListeners: MutableList<ImpactListener> = mutableListOf()

    protected open fun onIntersectionEnter(intersectingBox: Hittable) {
        if (intersectingBox !is Tank && intersectingBox !is Ground) return
        ground.destroy(this@BulletObject.position.x, strength = explosionWidth)
        currentScene.addGameObject(Explosion(position = this@BulletObject.position, radius = explosionWidth / 15))
        for (o in Game.instance.currentScene.children.values) {
            if (o == intersectingBox) {
                o.damage(damage)
            } else {
                val distance = o.position.distanceTo(this@BulletObject.position).toDouble()
                if (distance < explosionWidth) {
                    o.damage((1 / max(distance.pow(2) * .05, 1.0)) * damage)
                }
            }

            if (o is Tank) {
                o.adjustTransformToGround(ground)
            }
        }
        this@BulletObject.die()
    }

    fun addImpactListener(listener: ImpactListener) {
        impactListeners.add(listener)
    }

    fun removeImpactListener(listener: ImpactListener) {
        impactListeners.remove(listener)
    }

    override fun onDeath() {
        super.onDeath()
        for (listener in impactListeners) {
            listener(this@BulletObject.position)
        }
    }

    open val gravity = Vector3(0, -20, 0)

    open val explosionWidth: Double = 30.0
    protected open val damage: Double = 0.4

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
    private val dotVertices = ArrayDeque<Float>()
    private var dotTimer: Double = 0.0

    protected open val bulletRadius = 0.5f

    protected open val bulletMesh by lazy {
        Mesh(
            SphereGeometry(radius = bulletRadius),
            MeshPhongMaterial().apply {
                color = Color(0x111111)
            }
        ).apply { this@apply.position.set(tank.globalPosition) }
    }

    private var isNew: Boolean = true
    init {
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

    override var velocity: Vector3 = startVelocity * 0.75
    override var acceleration: Vector3
        get() = gravity
        set(_) {}

    private fun updateGeometry() {
        dots.setAttribute("position", BufferAttribute(Float32Array(dotVertices.toTypedArray()).asDynamic(), 3))
    }

    override fun update(delta: Double) {
        super.update(delta)
        dotTimer += delta
        if (dotTimer > 0.5) {
            dotTimer = 0.0
            dotVertices.apply {
                position.toFloatList().reversed().forEach {
                    addFirst(it)
                }
            }
            updateGeometry()
        }
        if (dotVertices.size > 3 * 10) {
            for (i in 1..3) {
                dotVertices.removeLast()
            }
            updateGeometry()
        }
        if (ground.heightAt(position.x) == null) { die() }
        if (isNew) { isNew = false }
    }

    companion object: AssetInitializer {


        override suspend fun initAssets() {
            ResourceManager.disc.await()
        }
    }
}