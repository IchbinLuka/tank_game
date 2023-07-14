package scenes

import GameMessageData
import core.Game
import core.Player
import core.gameobjects.GameObject
import core.multiplayer.MultiplayerSynchronized
import ext.*
import gameobjects.Background
import gameobjects.Ground
import gameobjects.Tank
import gameobjects.Tree
import gameobjects.bullets.Bullet
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
import kotlin.random.Random

data class GameState(val currentTank: Tank)

class MainSceneSingleplayer(
    private val numPlayers: Int = 3
) : GameScene(), Observable<GameState> by DefaultObservable() {

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

    private val aimingAidPlane = Mesh(PlaneGeometry(500, 500), MeshPhongMaterial().apply { visible = false })
    private val aimingCaster = Raycaster()
    private var aimIntersection = Vector3()
    private var aiming = true

    private var freeCamera = true

    private val ground = Ground(offset = Vector3(x = -225, y = 40), pointDistance = 3.0)
    private val groundRenderable = ground.renderable

    private val deadPlayers = ArrayDeque<Player>()

    private val tanks = MutableList(numPlayers) {
        val data = tankColors[it]
        Tank(
            player = Player(
                color = data.second,
                name = data.first,
                startInventory = hashMapOf(
                    Bullet.Normal to null,
                    Bullet.NoGravity to 5u,
                    Bullet.Big to 2u,
                    Bullet.Teleport to 2u
                )
            ),
            startPosition = Vector3(x = (200 / (numPlayers - 1)) * it - 100, z = 1.5),
        ).apply { addDeathListener { deadPlayers.addFirst(player) } }
    }

    private var currentTankIndex: Int = 0
    private val currentTank get() = tanks[currentTankIndex]

    private var currentBulletObject: BulletObject? = null

    val aimingStrength = ValueNotifier<Double>(0.0)
    private var aimingStrengthValue: Double by aimingStrength

    val currentTankNotifier = ValueNotifier(currentTank)

    private val topBar: MainTopBar = TODO() //MainTopBar(this)

    init {
        initListeners()
    }


    private lateinit var pointerMove: (Event) -> Unit

    private lateinit var clickListener: (Event) -> Unit

    private fun initListeners() {
        pointerMove = {
            it as PointerEvent
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
            if (currentBulletObject == null) {
                currentBulletObject = currentTank.shoot(aimingStrengthValue * MAX_POINTER_DISTANCE)
                currentBulletObject!!.let {
                    _children["bullet"] = it
                    renderable?.add(it.renderable)
                    it.addImpactListener { v ->
                        onImpact()
                    }
                }
            }
        }
        window.addEventListener("pointermove", pointerMove)

        Game.instance.gameContainer.addEventListener("click", clickListener)
    }

    private fun updateAimingStrength() {
        aimingStrengthValue = min(
            aimIntersection.distanceTo(currentTank.position).toDouble() / MAX_POINTER_DISTANCE,
            max(currentTank.health, 0.3)
        )
    }

    private fun centerCamera() {
        camera.lookAt(Vector3(camera.position.x * 0.5, y = 100))
    }

    private fun onImpact() {
        currentBulletObject = null
        nextTank()
    }

    private fun nextTank() {
        val aliveTanks = tanks.filter { it.active }
        if (aliveTanks.size <= 1) {
            for (tank in aliveTanks) {
                deadPlayers.addFirst(tank.player)
            }
            Game.instance.currentScene = GameEndScene(deadPlayers)
            return
        } else {
            do {
                currentTankIndex = (currentTankIndex + 1) % tanks.size
            } while (!currentTank.active)
        }
        updateAimingStrength()
        currentTankNotifier.value = currentTank
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
        val random = Random.Default
        for (tree in findObjects<Tree>()) {
            val x = random.nextDouble() * 600 - 300
            tree.position.x = x
            tree.position.y = ground.heightAt(tree.position.x) ?: 0.0
        }
        centerCamera()
    }

    override suspend fun loadAssets() {
        super.loadAssets()
        GUIHelper.pushReplacement(topBar)
    }

    override fun update(delta: Double) {
        super.update(delta)
        val currentTank = tanks.getOrNull(currentTankIndex)
        EventManager.keyboardEvents.events.forEach {
            if (currentBulletObject != null) return@forEach
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

    override fun dispose() {
        super.dispose()
        window.removeEventListener("pointermove", pointerMove)
        Game.instance.gameContainer.removeEventListener("click", clickListener)
    }

    companion object {
        val tankColors = listOf(
            "Green" to Color(0x11710F),
            "Orange" to Color(0xfc522f),
            "Red" to Color(0xef3434),
            "Blue" to Color(0x323cfc),
            "Pink" to Color(0xde43f9)
        )

        const val MAX_POINTER_DISTANCE = 125
    }
}