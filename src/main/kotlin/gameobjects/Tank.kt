package gameobjects

import Bullet
import core.CuboidHitBox
import core.Hittable
import core.Player
import core.currentScene
import core.gameobjects.GameObject
import ext.*
import fuelConsumption
import gameobjects.bullets.*
import gameobjects.particles.Fire
import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.html.dom.create
import three.CSS2DObject
import three.GLTF
import three.js.*
import ui.views.nameTag
import util.*
import kotlin.math.*
import kotlin.properties.Delegates

typealias Count = Int

open class Tank(
    val startPosition: Vector3,
    private val startRotation: Euler = Euler(),
    val player: Player,
    override val name: String = player.name,
    showLabel: Boolean = true
) : GameObject(), Observable<Bullet> by DefaultObservable(), Hittable, BulletTarget {
    override fun onBulletImpact(bullet: BulletObject) {}

    var moveDirection: Direction? = null

    final override val hitBox: CuboidHitBox = CuboidHitBox(
        size = Vector3(5, 10, 5),
        associatedObject = this
    ).apply {
        position.x = -2.5
    }


    override val initialChildren: Collection<GameObject> = listOf(hitBox)

    private val _bullets: MutableMap<Bullet, Count?> by player::inventory

    val bullets: Map<Bullet, Count?> by ::_bullets

    private val bulletSequence = ScrollableList(bullets.keys.sortedBy { it.ordinal })
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
        get() = mainBone?.position ?: Vector3()
        set(value) {
            mainBone?.position?.set(value)
        }

    private var gltf: GLTF? = null
    private var mainBone: Object3D? = null

    protected var direction = Direction.Right

    protected var cannon: Object3D? = null
    protected var cannonTip: Object3D? = null

    private val label = if (showLabel) CSS2DObject(document.create.nameTag(player)) else null

    val cannonTipPos: Vector3? get() = cannonTip?.getWorldPosition(Vector3())

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

    private val cannonRotation: Euler
        get() = cannon!!.getWorldEuler(Euler())

    private val cannonDirection: Vector3
        get() = with(cannonRotation) {
            Vector3(
                -sin(z.toDouble()),
                cos(z.toDouble()) * direction.vector3.x,
            ).normalize()
        }


    override suspend fun preloadAssets() {
        Log.v(TAG, "Loading tank")
        Bullet.initFactories()
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
        label?.let { renderable.add(it) }
        adjustLabel()
        rotation = startRotation
        cannon = mainBone?.children?.firstOrNull { it.name == "Cannon" }
        cannonTip = cannon?.children?.firstOrNull { it.name == "CannonTip" }
        if (mainBone == null || cannon == null) {
            Log.e(TAG, "Failed to identify mainBone and cannon")
            die()
        }

        Log.d(TAG, "Loaded tank")
        if (player.health <= 0.0) {
            addFire()
        }
        super.preloadAssets()
    }

    open fun aim(point: Vector2) {
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
        val bulletObject =  currentBullet.createObject.newGameObject(
            createBulletArgs(velocity)
        )
        if ((_bullets[currentBullet] ?: 1) <= 0) {
            nextBullet()
        }
        spawnedBullet = true
        return bulletObject
    }

    protected open fun createBulletArgs(velocity: Double): BulletObjectArgs = BulletObjectArgs(
        cannonDirection * velocity,
        this
    )

    open fun move(vector3: Vector3, deltaTime: Double = 1.0, ground: Ground) {
        if (player.fuel <= 0) return
        if (mainBone == null) return
        val previousPos = mainBone!!.position.clone()
        mainBone?.position?.let {
            val ascend = abs(ground.rotationAt(it.x))
            var newPos = it.clone()
            newPos = newPos.addScaledVector(vector3 * cos(ascend), deltaTime * SPEED)

            if (ground.heightAt(newPos.x) != null) {
                it.set(newPos)
            }
        }
        adjustTransformToGround(ground)
        player.fuel -= max(fuelConsumption(previousPos.distanceTo(mainBone!!.position).toDouble()), 0.0)
    }

    open fun adjustTransformToGround(ground: Ground) {
        mainBone?.position?.let {
            val pos = it.x
            it.y = ground.heightAt(pos)!!
            val rotation = ground.rotationAt(pos) + 1.5 *  PI
            mainBone?.rotation?.z = if (direction == Direction.Right) rotation else (2 * PI - rotation).toFloat()
            if (it.y < 0) {
                damage(1.0)
            }
        }
        adjustLabel()
        updateFireRotation()
    }

    private fun adjustLabel() {
        label?.position?.set(renderable.worldToLocal(globalPosition - Vector3(0, 5, 0)))
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
        fire.preloadAssets()
        _children["fire"] = fire
        val fireRenderable = fire.renderable!!
        renderable.add(fireRenderable)
        updateFireRotation()
    }

    fun updateFireRotation() {
        fire?.renderable?.lookAt(fire!!.globalPosition + Vector3(z = 1))
    }

    protected fun setDirection(direction: Direction) {
        if (direction != this.direction) {
            mainBone?.rotateX(PI)
        }
        this.direction = direction
    }

    override fun onSceneReady() {
        super.onSceneReady()
        Log.d(TAG, "hitbox vertices: ${hitBox.vertices.map { it.toFloatList() }}")
    }

    override fun update(delta: Double) {
        super.update(delta)
        if (spawnedBullet) {
            spawnedBullet = false
        }
        if (moveDirection != null) {
            currentScene.findObject<Ground>()?.let {
                move(moveDirection!!.vector3, delta, it)
            }
        }
    }

    protected fun switchDirection() {
        setDirection(direction.inverse)
    }

    override fun toString(): String {
        return "Tank(${player.name}), children: $children"
    }

    companion object {
        const val SPEED = 3.0
    }
}