package gameobjects

import core.CuboidHitBox
import core.Hittable
import core.gameobjects.GameObject
import ext.*
import gameobjects.bullets.Bullet
import gameobjects.bullets.BulletObject
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
    val color: Color = Color(0x338530),
    val startPosition: Vector3,
    val startRotation: Euler = Euler(),
    val name: String = "Unknown"
) : GameObject(), Observable<Bullet> by DefaultObservable(), Hittable {

    override val hitBox: CuboidHitBox = CuboidHitBox(size = Vector3(5, 10, 5), associatedObject = this, showVertices = false).also {
        it.addIntersectionListener(::onIntersection)
        it.position = Vector3(x = -2.5)
    }

    override val _children: MutableMap<String, GameObject> = hashMapOf("hitBox" to hitBox)

    private val _bullets: HashMap<Bullet, Count?> = hashMapOf(
        Bullet.Normal to null,
        Bullet.NoGravity to 5
    )

    val bullets: Map<Bullet, Count?> by ::_bullets

    private val bulletSequence = ScrollableList(bullets.keys)
    var currentBullet: Bullet by Delegates.observable(bulletSequence.current, ::notifyObservers)

    override val invincible: Boolean = false

    override var renderable: Object3D = Group()

    private fun onIntersection(other: Hittable) {
        Log.d(TAG, "intersected with ${other.TAG}")
    }

    fun nextBullet() {
        bulletSequence.next()
        bulletSequence.nextUntil { (bullets[it] ?: 1) <= 0 }
        currentBullet = bulletSequence.current
    }

    fun previousBullet() {
        bulletSequence.previous()
        bulletSequence.previousUntil { (bullets[it] ?: 1) <= 0 }
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

    val active: Boolean
        get() = health > 0

    override suspend fun initVisual() {
        super.initVisual()
        Log.v(TAG, "Loading tank")
        gltf = ResourceManager.tank.await()
        gltf?.scene?.apply {
            enableShadows()
            applyCustomMat(MeshToonMaterial().apply {
                color = this@Tank.color
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

    fun aim(point: Vector2) {
        if (cannon?.rotation == null) return
        val globalpos = cannon!!.getWorldPosition(Vector3())
        val delta = Vector2(point.x - globalpos.x, point.y - globalpos.y)
        var rotation = atan(delta.y.toFloat().absoluteValue / delta.x.toFloat().absoluteValue)
        val globalRot = cannon!!.getWorldEuler(Euler())
        if (delta.x.toFloat() * delta.y.toFloat() * direction.vector3.x.toFloat() < 0) {
            rotation *= -1
        }
        if (delta.x.toFloat() < 0) {
            rotation += PI.toFloat()
        }
        val angle = rotation - globalRot.z - 0.5 * PI
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
        val worldPosition = cannon!!.getWorldPosition(Vector3())
        val worldRotation = cannon!!.getWorldEuler(Euler())
        if (_bullets[currentBullet] != null) {
            _bullets[currentBullet] = _bullets[currentBullet]!! - 1
        }
        val bulletObject =  currentBullet.createObject(
            Vector3(
                -sin(worldRotation.z.toDouble()) * velocity,
                cos(worldRotation.z.toDouble()) * velocity * direction.vector3.x,
            ),
            Vector3(worldPosition.x, worldPosition.y)
        )
        if ((_bullets[currentBullet] ?: 1) <= 0) {
            nextBullet()
        }
        return bulletObject
    }

    fun move(vector3: Vector3, deltaTime: Double = 1.0, ground: Ground) {
        mainBone?.position?.let {
            val ascend = abs(ground.rotationAt(it.x))
            if (true || ascend > 0.5 * PI) {
                it.addScaledVector(vector3, deltaTime)
            }
        }
        adjustTransformToGround(ground)
    }

    fun adjustTransformToGround(ground: Ground) {
        mainBone?.position?.let {
            val pos = mainBone!!.position.x
            it.y = ground.heightAt(pos)!!
            val rotation = ground.rotationAt(pos)
            mainBone?.rotation?.z = if (direction == Direction.Right) rotation else (2 * PI - rotation).toFloat()
        }
        fire?.renderable?.rotation?.z = -renderable.rotation.z.toDouble()
    }

    override fun onDeath() {
        super.onDeath()
        MainScope().launch {
            val fire = Fire(radius = 3.0, position = Vector3())
            fire.initVisual()
            _children["fire"] = fire
            val fireRenderable = fire.renderable!!
            renderable.add(fireRenderable)
            fire.position.set(Vector3())
            fire.renderable?.rotation?.z = -renderable.rotation.z.toDouble()
        }
    }

    private fun setDirection(direction: Direction) {
        if (direction != this.direction) {
            mainBone?.rotateX(PI)
        }
        this.direction = direction
    }

    private fun switchDirection() {
        setDirection(direction.inverse)
    }
}