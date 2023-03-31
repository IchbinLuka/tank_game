package gameobjects

import core.CuboidHitBox
import core.Hittable
import core.Player
import core.gameobjects.GameObject
import ext.*
import gameobjects.bullets.Bullet
import gameobjects.bullets.BulletObject
import gameobjects.bullets.BulletTarget
import gameobjects.particles.Fire
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import three.GLTF
import three.js.*
import util.*
import kotlin.math.*
import kotlin.properties.Delegates

typealias Count = Int

class Tank(
    val startPosition: Vector3,
    private val startRotation: Euler = Euler(),
    val player: Player
) : GameObject(), Observable<Bullet> by DefaultObservable(), Hittable, BulletTarget {
    override fun onBulletImpact(bullet: BulletObject) {}

    override val hitBox: CuboidHitBox = object: CuboidHitBox(size = Vector3(5, 10, 5), associatedObject = this@Tank, showVertices = false) {
        init {
            position = Vector3(x = -2.5)
        }

        override fun onIntersectionEnter(intersectingBox: Hittable) {
            Log.d(TAG, "intersected with ${intersectingBox.TAG}")
            if (spawnedBullet) return
            super.onIntersectionEnter(intersectingBox)
        }
    }

    override val _children: MutableMap<String, GameObject> = hashMapOf("hitBox" to hitBox)

    private val _bullets: MutableMap<Bullet, Count?> by player::inventory

    val bullets: Map<Bullet, Count?> by ::_bullets

    private val bulletSequence = ScrollableList(bullets.keys)
    var currentBullet: Bullet by Delegates.observable(bulletSequence.current, ::notifyObservers)

    override val invincible: Boolean = false

    private var spawnedBullet: Boolean = false

    override var renderable: Object3D = Group()

    private val deathListeners: MutableList<() -> Unit> = mutableListOf()

    fun addDeathListener(listener: () -> Unit) {
        deathListeners.add(listener)
    }

    fun removeDeathListener(listener: () -> Unit) {
        deathListeners.remove(listener)
    }

    fun nextBullet() {
        bulletSequence.next()
        bulletSequence.nextWhile { (bullets[it] ?: 1) <= 0 }
        currentBullet = bulletSequence.current
    }

    fun previousBullet() {
        bulletSequence.previous()
        bulletSequence.previousWhile { (bullets[it] ?: 1) <= 0 }
        currentBullet = bulletSequence.current
    }

    override var position: Vector3
        get() = mainBone?.position!!
        set(value) {
            mainBone?.position?.set(value)
        }

    private var gltf: GLTF? = null
    private var mainBone: Object3D? = null

    private var direction = Direction.Right

    private var cannon: Object3D? = null

    private val fire: Fire?
        get() = findObject("fire")

    var rotation: Euler
        set(value) {
            mainBone?.setRotationFromEuler(value)
        }
        get() = mainBone!!.rotation

    override var health: Double by player::health

    val active: Boolean
        get() = health > 0

    val cannonRotation: Euler
        get() = cannon!!.getWorldEuler(Euler())

    val cannonDirection: Vector3
        get() = with(cannonRotation) {
            Vector3(
                -sin(z.toDouble()),
                cos(z.toDouble()) * direction.vector3.x,
            ).normalize()
        }

    override suspend fun initVisual() {
        super.initVisual()
        Log.v(TAG, "Loading tank")
        gltf = ResourceManager.tank.await()
        gltf?.scene?.apply {
            enableShadows()
            applyCustomMat(MeshToonMaterial().apply {
                color = this@Tank.player.color
            })
        }
        mainBone = gltf?.scene?.children?.firstOrNull { it.name == "Main" }
        mainBone?.position?.set(startPosition)
        renderable = mainBone!!
        rotation = startRotation
        cannon = mainBone?.children?.firstOrNull { it.name == "Cannon" }
        if (mainBone == null || cannon == null) {
            Log.e(TAG, "Failed to identify mainBone and cannon")
            die()
        }
        Log.d(TAG, "Loaded tank")
    }

    override suspend fun preloadAssets() {
        super.preloadAssets()
        if (player.health <= 0.0) {
            addFire()
        }
    }

    fun aim(point: Vector2) {
        if (cannon?.rotation == null) return
        val globalpos = cannon!!.getWorldPosition(Vector3())
        val delta = Vector2(point.x - globalpos.x, point.y - globalpos.y)
        var rotation = atan(delta.y.toFloat().absoluteValue / delta.x.toFloat().absoluteValue)
        val (_, _, globalRot) = cannon!!.getWorldEuler(Euler())
        if (delta.x.toFloat() * delta.y.toFloat() * direction.vector3.x.toFloat() < 0) {
            rotation *= -1
        }
        if (delta.x.toFloat() < 0) {
            rotation += PI.toFloat()
        }
        val angle = rotation - globalRot - 0.5 * PI
        if (toUnitCircle(cannon!!.rotation.z.toDouble() + angle) < 2.8) {
            cannon?.rotateOnWorldAxis(Vector3(0, 0, 1), angle)
        }
        if (cannon!!.rotation.z.toFloat() > 2.3f) {
            switchDirection()
            // TODO: Aim to correct spot after turning
        }
    }

    fun shoot(velocity: Double): BulletObject? {
        if (cannon == null) return null
        if (_bullets[currentBullet] != null) {
            _bullets[currentBullet] = _bullets[currentBullet]!! - 1
        }
        val bulletObject =  currentBullet.createObject(
            cannonDirection * velocity, this
        )
        if ((_bullets[currentBullet] ?: 1) <= 0) {
            nextBullet()
        }
        spawnedBullet = true
        return bulletObject
    }

    fun move(vector3: Vector3, deltaTime: Double = 1.0, ground: Ground) {
        mainBone?.position?.let {
            val ascend = abs(ground.rotationAt(it.x))
            var newPos = it.clone()
            newPos = newPos.addScaledVector(vector3 * cos(ascend), deltaTime)

            if (ground.heightAt(newPos.x) != null) {
                it.set(newPos)
            }
        }
        adjustTransformToGround(ground)
    }

    fun adjustTransformToGround(ground: Ground) {
        mainBone?.position?.let {
            val pos = mainBone!!.position.x
            it.y = ground.heightAt(pos)!!
            val rotation = ground.rotationAt(pos) + 1.5 *  PI
            mainBone?.rotation?.z = if (direction == Direction.Right) rotation else (2 * PI - rotation).toFloat()
        }
        updateFireRotation()
    }

    override fun onDeath() {
        super.onDeath()
        for (listener in deathListeners) {
            listener()
        }
        MainScope().launch {
            addFire()
        }
    }

    private suspend fun addFire() {
        val fire = Fire(radius = 3.0, position = Vector3())
        fire.initVisual()
        _children["fire"] = fire
        val fireRenderable = fire.renderable!!
        Log.d(TAG, "adding fire renderable")
        renderable.add(fireRenderable)
        updateFireRotation()
    }

    fun updateFireRotation() {
        Log.d(TAG, "adjusting fire rotation")
        fire?.renderable?.lookAt(fire!!.globalPosition + Vector3(z = 1))
    }

    private fun setDirection(direction: Direction) {
        if (direction != this.direction) {
            mainBone?.rotateX(PI)
        }
        this.direction = direction
    }

    override fun update(delta: Double) {
        super.update(delta)
        if (spawnedBullet) { spawnedBullet = false }
    }

    private fun switchDirection() {
        setDirection(direction.inverse)
    }

    override fun toString(): String {
        return "Tank(${player.name}), children: $children"
    }
}