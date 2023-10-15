package scenes

import Bullet
import GameMessageData
import core.Game
import core.Player
import core.gameobjects.GameObject
import ext.TAG
import ext.aspectRatio
import ext.enableShadows
import ext.toVector3
import gameobjects.Background
import gameobjects.Ground
import gameobjects.Tank
import gameobjects.Tree
import gameobjects.bullets.BulletObject
import kotlinx.browser.window
import three.js.*
import ui.MainSceneController
import util.*
import kotlin.collections.ArrayDeque
import kotlin.collections.Collection
import kotlin.collections.List
import kotlin.collections.filter
import kotlin.collections.first
import kotlin.collections.forEach
import kotlin.collections.getOrNull
import kotlin.collections.isNotEmpty
import kotlin.collections.map
import kotlin.collections.mutableListOf
import kotlin.collections.set
import kotlin.collections.zip
import kotlin.math.max
import kotlin.math.min

abstract class MainScene(
    val startData: GameMessageData.GameStartData
) : GameScene() {
    override val scene: Scene = Scene().apply {
        background = Color(0x3333ff)
        val light = DirectionalLight(0xFDFFD9, 1).apply {
            position.set(-30, 70, 40)
            castShadow = true
            enableShadows()
        }
        add(light)
        add(AmbientLight(0x404040, 0.1))
        add(HemisphereLight(skyColor = 0x47C4F3, groundColor = 0xABAF9E, intensity = 0.5))
    }
    override val camera = PerspectiveCamera(75, window.aspectRatio, 0.1, 2000).apply {
        window.addEventListener("resize", {
            position.z = 250.0 / window.aspectRatio
        })
        position.z = 250.0 / window.aspectRatio
        position.y = 120.0
        lookAt(Vector3(y = 100))
    }

    override val initialChildren: Collection<GameObject> by lazy {
        mutableListOf(
            ground,
            Background(name = "background", scene = scene),
            BackgroundLandscape()
        ).apply {
            addAll(tanks)
            addAll(startData.trees.map { Tree(startPosition = Vector3(x = it.pos), name = it.name) })
        }
    }

    private val aimingAidPlane = Mesh(PlaneGeometry(500, 500), MeshPhongMaterial().apply { visible = false })
    private val aimingCaster = Raycaster()
    private var aimIntersection = Vector3()
    private var aiming = true

    private var freeCamera = true

    private val ground =
        Ground(offset = Vector3(x = -225, y = 40), pointDistance = 3.0, data = startData.groundData.data)
    private val groundRenderable = ground.renderable

    protected val deadPlayers = ArrayDeque<Player>()

    protected fun isGameOver(): Boolean {
        return deadPlayers.size >= tanks.size - 1
    }


    protected abstract val tanks: List<Tank>

    abstract fun isProxy(tank: GameMessageData.TankData): Boolean

    protected var currentTankIndex: Int = 0
    protected val currentTank get() = tanks[currentTankIndex]

    protected var currentBulletObject: BulletObject? = null

    val aimingStrength = ValueNotifier<Double>(0.0)
    private var aimingStrengthValue: Double by aimingStrength

    val currentTankNotifier by lazy {
        ValueNotifier(currentTank)
    }

    private val topBar = MainSceneController(this)


    fun performShot() {
        if (currentBulletObject == null && hasTurn) {
            currentBulletObject = currentTank.shoot(aimingStrengthValue * MAX_POINTER_DISTANCE)
            currentBulletObject!!.let {
                _children["bullet"] = it
                renderable?.add(it.renderable)
                it.addImpactListener { v ->
                    onImpact()
                }
            }
            onBulletCreation(currentBulletObject!!.type, currentBulletObject!!.arg.startVelocity)
        }
    }

    fun updateAimingPoint(point: Vector2) {
        if (!hasTurn || currentBulletObject != null) return

        if (aiming) {
            updateAimingStrength()

            aimingCaster.setFromCamera(point, camera)
            val intersection = aimingCaster.intersectObject(aimingAidPlane)
            if (intersection.isNotEmpty()) {
                aimIntersection = intersection.first().point
                currentTank.aim(Vector2(aimIntersection.x, aimIntersection.y))
                if (!freeCamera) {
                    camera.position.x = point.x * 40
                    centerCamera()
                }
            }
        }
    }

    protected fun updateAimingStrength() {
        aimingStrengthValue = min(
            aimIntersection.distanceTo(currentTank.position).toDouble() / MAX_POINTER_DISTANCE,
            max(currentTank.health, 0.3)
        )
    }

    private fun centerCamera() {
        camera.lookAt(Vector3(camera.position.x * 0.5, y = 100))
    }

    /// Called when a bullet hits the ground. Resets the current bullet object.
    protected open fun onImpact() {
        currentBulletObject = null
    }

    protected open fun endGame() {
        for (tank in tanks.filter { it.active }) {
            deadPlayers.addFirst(tank.player)
        }
        Log.d(TAG, deadPlayers.map { it.name })
        Game.instance.currentScene = GameEndScene(deadPlayers)
    }

    override suspend fun preloadAssets() {
        super.preloadAssets()
        // _children.values.forEach { o -> o.renderable?.let { scene.add(it) } }
        scene.add(groundRenderable, aimingAidPlane)
        centerCamera()
        scene.background = Color(0x64DBFF)
    }

    override fun onSceneReady() {
        super.onSceneReady()
        for (tank in tanks) {
            tank.adjustTransformToGround(ground)
        }
        for ((tree, startTree) in findObjects<Tree>() zip startData.trees) {
            tree.position.x = startTree.pos
            tree.position.y = ground.heightAt(tree.position.x) ?: 0.0
        }
        centerCamera()
    }

    override suspend fun loadAssets() {
        super.loadAssets()
        GUIHelper.pushReplacement(topBar)
    }

    abstract val hasTurn: Boolean

    override fun update(delta: Double) {
        super.update(delta)

        if (!hasTurn) return
        val currentTank = tanks.getOrNull(currentTankIndex)
        EventManager.keyboardEvents.events.forEach {
            when (it.code) {
                KeyCodes.KEY_A, KeyCodes.ARROW_LEFT -> {
                    currentTank?.move(Direction.Left.vec3.toVector3(), delta, ground)
                }
                KeyCodes.KEY_D, KeyCodes.ARROW_RIGHT -> {
                    currentTank?.move(Direction.Right.vec3.toVector3(), delta, ground)
                }
            }
        }
    }

    protected open fun onBulletCreation(type: Bullet, velocity: Vector3) {}

    companion object {
        const val MAX_POINTER_DISTANCE = 125
    }
}