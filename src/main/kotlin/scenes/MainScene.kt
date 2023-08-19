package scenes

import Bullet
import GameMessageData
import core.Game
import core.Player
import core.gameobjects.GameObject
import ext.*
import gameobjects.Background
import gameobjects.Ground
import gameobjects.Tank
import gameobjects.Tree
import gameobjects.bullets.BulletObject
import kotlinx.browser.window
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.pointerevents.PointerEvent
import three.js.*
import ui.MainTopBar
import util.*
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
    override val camera = PerspectiveCamera(75, window.aspectRatio, 0.1, 1000).apply {
        position.z = 150.0
        position.y = 120.0
        lookAt(Vector3(y = 100))
    }

    override val initialChildren: Collection<GameObject> by lazy {
        mutableListOf(
            ground,
            Background(name = "background"),
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

    protected val ground = Ground(offset = Vector3(x = -225, y = 40), pointDistance = 3.0, data = startData.groundData.data)
    private val groundRenderable = ground.renderable

    protected val deadPlayers = ArrayDeque<Player>()

    protected fun isGameOver(): Boolean {
        return deadPlayers.size >= tanks.size - 1
    }


    protected open val tanks: List<Tank> = startData.state.tanks.map {
        Tank(
            player = Player.fromTankData(it),
            startPosition = it.pos.toVector3(),
        ).apply {
            addDeathListener {
                deadPlayers.addFirst(player)
                if (isGameOver()) {
                    endGame()
                }
            }
        }
    }.toMutableList()

    abstract fun isProxy(tank: GameMessageData.TankData): Boolean

    protected var currentTankIndex: Int = 0
    protected val currentTank get() = tanks[currentTankIndex]

    protected var currentBulletObject: BulletObject? = null

    val aimingStrength = ValueNotifier<Double>(0.0)
    private var aimingStrengthValue: Double by aimingStrength

    val currentTankNotifier by lazy { ValueNotifier(currentTank) }

    private val topBar = MainTopBar(this)

    init {
        initListeners()
    }


    private lateinit var pointerMove: (Event) -> Unit

    private lateinit var clickListener: (Event) -> Unit

    private fun initListeners() {
        pointerMove = pointerMove@ {
            it as PointerEvent
            if (!hasTurn || currentBulletObject != null) return@pointerMove

            val aimingPoint = Vector2()
            val containerBox = Game.instance.gameContainer.getBoundingClientRect()
            val x = it.clientX.toDouble() - containerBox.left
            val y = it.clientY.toDouble() - containerBox.top
            aimingPoint.x = ( x / containerBox.width ) * 2 - 1
            aimingPoint.y = - ( y / containerBox.height ) * 2 + 1

            if (aiming) {
                updateAimingStrength()
                aimingCaster.setFromCamera(aimingPoint, camera)
                val intersection = aimingCaster.intersectObject(aimingAidPlane)
                if (intersection.isNotEmpty()) {
                    aimIntersection = intersection.first().point
                    currentTank.aim(Vector2(aimIntersection.x, aimIntersection.y))
                    if (!freeCamera) {
                        camera.position.x = aimingPoint.x * 40
                        centerCamera()
                    }
                }
            }
        }
        clickListener = { event ->
            event as MouseEvent
            if (currentBulletObject == null && hasTurn) {
                currentBulletObject = currentTank.shoot(aimingStrengthValue * MAX_POINTER_DISTANCE)
                currentBulletObject!!.let {
                    _children["bullet"] = it
                    renderable?.add(it.renderable)
                    it.addImpactListener { v ->
                        onImpact()
                    }
                }
                onBulletCreation(currentBulletObject!!.type, currentBulletObject!!.velocity)
            }
        }
        window.addEventListener("pointermove", pointerMove)

        Game.instance.gameContainer.addEventListener("click", clickListener)
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

    protected fun endGame() {
        for (tank in tanks.filter { it.active }) {
            deadPlayers.addFirst(tank.player)
        }
        Game.instance.currentScene = GameEndScene(deadPlayers)
    }

    override suspend fun preloadAssets() {
        super.preloadAssets()
        scene.add(ResourceManager.testLandscape.await().scene.apply {
            enableShadows()
            applyCustomMat(MeshToonMaterial().apply { color = Color(0x195917) })
            scale.set(Vector3(4, 4, 4))
        })
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
                    currentTank?.move(Vector3(-6, 0, 0), delta, ground)
                }
                KeyCodes.KEY_D, KeyCodes.ARROW_RIGHT -> {
                    currentTank?.move(Vector3(6, 0, 0), delta, ground)
                }
            }
        }
    }

    protected open fun onBulletCreation(type: Bullet, velocity: Vector3) {}

    override fun dispose() {
        super.dispose()
        window.removeEventListener("pointermove", pointerMove)
        Game.instance.gameContainer.removeEventListener("click", clickListener)
    }

    companion object {
        const val MAX_POINTER_DISTANCE = 125
    }
}