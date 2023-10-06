package gameobjects.bullets

import Bullet
import GameMessage
import GameMessageData
import core.*
import core.gameobjects.GameObject
import core.gameobjects.SynchronizedInitializer
import core.multiplayer.MessageBroadcaster
import core.multiplayer.MultiplayerSynchronized
import core.multiplayer.RateLimitedBroadcaster
import ext.*
import gameobjects.GameObjectFactory
import gameobjects.Ground
import gameobjects.Tank
import gameobjects.particles.Explosion
import getBulletDamage
import kotlinx.browser.window
import org.khronos.webgl.Float32Array
import three.js.*
import util.Log
import util.ResourceManager
import kotlin.math.pow

typealias ImpactListener = (Vector3) -> Unit

interface BulletTarget : Hittable {
    fun onBulletImpact(bullet: BulletObject)
}

data class BulletObjectArgs(
    val startVelocity: Vector3,
    val tank: Tank,
    val proxy: Boolean = false,
    val synchronized: Boolean = false,
    val name: String = "bullet"
)

open class BulletObject protected constructor(
    val arg: BulletObjectArgs
) : GameObject(), Hittable, SynchronizedInitializer, MultiplayerSynchronized {

    open class Factory : GameObjectFactory<BulletObject, BulletObjectArgs>() {
        lateinit var disc: Texture
        override suspend fun loadAssets() {
            Log.d(TAG, "loading assets for bullet")
            disc = ResourceManager.disc.await()
        }

        override fun createGameObject(arg: BulletObjectArgs): BulletObject {
            return BulletObject(arg)
        }
    }

    override val name: String = arg.name

    protected val tank: Tank = arg.tank

    private val spawnTime = window.performance.now()

    override val topic: String = "${tank.player.name}_bullet"
    val proxy = arg.proxy
    val synchronized = arg.synchronized

    private val broadcaster: RateLimitedBroadcaster? =
        if (arg.synchronized) RateLimitedBroadcaster(3, ConnectionManager) else null
    private val urgentBroadcaster: MessageBroadcaster? = if (arg.synchronized) ConnectionManager else null

    // region - HitBox -

    final override val hitBox: HitBox = object : HitBox() {
        override val name: String = "hitBox"
        override val associatedObject: Hittable
            get() = this@BulletObject

        override val vertices: Iterable<Vector3>
            get() = listOf(this@BulletObject.position)

        override fun containsPoint(point: Vector3): Boolean = false

        override fun onIntersectionEnter(intersectingBox: Hittable) {
            super.onIntersectionEnter(intersectingBox)
            Log.d(this@BulletObject.TAG, "intersected with ${intersectingBox.TAG}")
            if (isNew || this@BulletObject.proxy) return
            this@BulletObject.onIntersectionEnter(intersectingBox)
        }
    }

    override val initialChildren: Collection<GameObject> = listOf(
        hitBox
    )

    // endregion

    open val type: Bullet = Bullet.Normal

    open val gravity = Vector3(0, -20, 0)

    private val explosionWidth: Double get() = type.explosionWidth
    private val damage: Double get() = type.damage

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
        ).apply { this@apply.position.set(tank.cannonTipPos!!) }
    }

    private var isNew: Boolean = true

    protected val ground: Ground = Game.instance.currentScene.findObject<Ground>()!!

    private val impactListeners: MutableList<ImpactListener> = mutableListOf()

    open val startVelocity: Vector3 = arg.startVelocity * 0.75

    private val initialPos = tank.cannonTipPos!!


    init {
        initConnection(arg)
    }

    protected open fun broadcastImpact(intersectingBox: Hittable) {
        if (proxy) return
        urgentBroadcaster?.broadcast(
            GameMessage.fromGameMessageData(
                GameMessageData.BulletImpact(
                    pos = GameMessageData.Vec3.fromVector3(position),
                    intersectingName = intersectingBox.name,
                    type = type
                ),
                topic
            )
        )
    }

    private fun initConnection(arg: BulletObjectArgs) {
        if (arg.synchronized) {
            ConnectionManager.register(this)
        }
    }

    protected open fun onIntersectionEnter(intersectingBox: Hittable) {
        Log.d(TAG, "onIntersectionEnter: $intersectingBox")
        if (intersectingBox !is BulletTarget) return
        if (!proxy) {
            Log.d(TAG, "broadcasting bullet impact")
            broadcastImpact(intersectingBox)
        }
        if (!proxy) {
            ground.destroy(this@BulletObject.position.x, strength = explosionWidth, broadcast = true)
        }
        currentScene.addGameObject(Explosion(position = this@BulletObject.position, radius = explosionWidth / 15))
        intersectingBox.onBulletImpact(this)
        for (o in Game.instance.currentScene.children.values) {
            if (o == intersectingBox) {
                o.damage(damage)
            } else {
                val distance = o.position.distanceTo(this@BulletObject.position).toDouble()
                o.damage(getBulletDamage(distance, type))
            }

            if (o is Tank) {
                o.adjustTransformToGround(ground)
            }
        }
        Log.d(TAG, "destroying bullet")
        die()
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

    override fun dispose() {
        super.dispose()
        ConnectionManager.unregister(this)
    }

    private fun updateGeometry() {
        dots.setAttribute("position", BufferAttribute(Float32Array(dotVertices.toTypedArray()).asDynamic(), 3))
    }

    private fun posAtTime(t: Double): Vector3 {
        return initialPos + startVelocity * t + gravity * t.pow(2) * 0.5
    }

    override fun update(delta: Double) {
        super.update(delta)

        position = posAtTime((window.performance.now() - spawnTime) * 0.001)

        dotTimer += delta
        if (dotTimer > 0.5) {
            dotTimer = 0.0
            Log.d(TAG, "adding dot")
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
        if (ground.heightAt(position.x) == null) {
            despawn()
            return
        }
        if (isNew) {
            isNew = false
        }
    }

    /**
     * Kills the bullet and broadcasts the death.
     */
    protected fun despawn() {
        if (proxy) return
        die()
        urgentBroadcaster?.broadcast(
            GameMessage.fromGameMessageData(
                GameMessageData.BulletDespawn,
                topic
            )
        )

    }

    companion object : Factory()

    override fun init() {
        Log.d(TAG, "initializing dots")
        renderable.add(Points(dots,
            PointsMaterial().apply {
                color = Color(0xffffff)
                size = 1.5 * window.devicePixelRatio
                transparent = true
                alphaMap = disc
            }
        ))
        bulletMesh.add(hitBox.renderable)
    }

    override fun onMessage(message: GameMessageData) {
        when (message) {

            is GameMessageData.BulletImpact -> {
                currentScene.findObject<BulletTarget>(message.intersectingName)?.let {
                    onIntersectionEnter(it)
                } ?: Log.w(TAG, "could not find intersecting object ${message.intersectingName}")
            }

            is GameMessageData.Death, GameMessageData.BulletDespawn -> {
                die()
            }

            else -> {}
        }
    }
}