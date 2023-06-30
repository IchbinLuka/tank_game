package gameobjects.bullets

import GameMessage
import GameMessageData
import core.*
import core.gameobjects.GameObject
import core.gameobjects.MovingGameObject
import core.gameobjects.SynchronizedInitializer
import core.multiplayer.MultiplayerSynchronized
import core.multiplayer.RateLimitedBroadcaster
import ext.*
import gameobjects.GameObjectFactory
import gameobjects.Ground
import gameobjects.Tank
import gameobjects.bullets.BigBulletObject.Companion.disc
import gameobjects.particles.Explosion
import kotlinx.browser.window
import org.khronos.webgl.Float32Array
import three.js.*
import util.AssetInitializer
import util.Log
import util.ResourceManager
import kotlin.math.max
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
)

open class BulletObject protected constructor(
    arg: BulletObjectArgs
) : MovingGameObject(), Hittable, SynchronizedInitializer, MultiplayerSynchronized {

    open class Factory : GameObjectFactory<BulletObject, BulletObjectArgs>() {
        lateinit var disc: Texture
        override suspend fun loadAssets() {
            disc = ResourceManager.disc.await()
        }

        override fun createGameObject(arg: BulletObjectArgs): BulletObject {
            return BulletObject(arg)
        }
    }


    protected val tank: Tank = arg.tank

    override val topic: String = "${tank.player.name}_bullet"
    val proxy = arg.proxy

    protected val broadcaster: RateLimitedBroadcaster? =
        if (arg.synchronized) RateLimitedBroadcaster(3, ConnectionManager) else null

    // region - HitBox -

    final override val hitBox: HitBox = object : HitBox() {
        override val associatedObject: Hittable
            get() = this@BulletObject

        override val vertices: Iterable<Vector3>
            get() = listOf(this@BulletObject.position)

        override fun containsPoint(point: Vector3): Boolean = false

        override fun onIntersectionEnter(intersectingBox: Hittable) {
            super.onIntersectionEnter(intersectingBox)
            Log.d(this@BulletObject.TAG, "intersected with ${intersectingBox.TAG}")
            if (isNew) return
            this@BulletObject.onIntersectionEnter(intersectingBox)
        }
    }

    override val _children: MutableMap<String, GameObject> = hashMapOf("hitBox" to hitBox)

    // endregion

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
        ).apply { this@apply.position.set(tank.cannonTipPos!!) }
    }

    private var isNew: Boolean = true

    protected val ground: Ground = Game.instance.currentScene.findObject<Ground>()!!

    private val impactListeners: MutableList<ImpactListener> = mutableListOf()

    override var velocity: Vector3 = arg.startVelocity * 0.75
    override var acceleration: Vector3
        get() = gravity
        set(_) {}


    init {
        initConnection(arg)
    }

    private fun initConnection(arg: BulletObjectArgs) {
        if (arg.proxy) {
            ConnectionManager.register(this)
        }
    }

    protected open fun onIntersectionEnter(intersectingBox: Hittable) {
        if (intersectingBox !is BulletTarget) return
        if (!proxy) {
            ground.destroy(this@BulletObject.position.x, strength = explosionWidth)
        }
        currentScene.addGameObject(Explosion(position = this@BulletObject.position, radius = explosionWidth / 15))
        intersectingBox.onBulletImpact(this)
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
        if (ground.heightAt(position.x) == null) {
            die()
        }
        if (isNew) {
            isNew = false
        }

        broadcaster?.broadcast(
            GameMessage.fromGameMessageData(
                GameMessageData.GameObjectMoveUpdate(
                    pos = GameMessageData.Vec3.fromVector3(position),
                    velocity = GameMessageData.Vec3.fromVector3(velocity),
                    acceleration = GameMessageData.Vec3.fromVector3(acceleration),
                    rot = GameMessageData.Vec3.fromVector3(globalRotation.toVector3()),
                ),
                topic = topic
            ),
        )
    }

    companion object : Factory()

    override fun init() {
        renderable.add(Points(dots,
            PointsMaterial().apply {
                color = Color(0xffffff)
                size = 1.5 * window.devicePixelRatio
                alphaMap = disc
                map = disc
                alphaTest = 0.5
            }
        ))
        bulletMesh.add(hitBox.renderable)
    }

    override fun onMessage(message: GameMessageData) {
        when (message) {
            is GameMessageData.GameObjectMoveUpdate -> {
                position = message.pos.toVector3()
                velocity = message.velocity.toVector3()
                acceleration = message.acceleration.toVector3()
            }

            is GameMessageData.GameObjectMoveStart -> {
                position = message.pos.toVector3()
                velocity = message.velocity.toVector3()
                acceleration = message.acceleration.toVector3()
            }

            else -> {}
        }
    }
}