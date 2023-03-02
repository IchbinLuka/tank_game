package scenes

import core.Game
import core.gameobjects.GameObject
import ext.applyCustomMat
import ext.aspectRatio
import ext.enableShadows
import ext.set
import gameobjects.Background
import gameobjects.Ground
import gameobjects.Tank
import gameobjects.bullets.BulletObject
import kotlinx.browser.window
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.pointerevents.PointerEvent
import three.js.*
import ui.MainTopBar
import util.DefaultObservable
import util.EventManager
import util.Observable
import util.ResourceManager
import kotlin.collections.set
import kotlin.properties.Delegates

data class GameState(val currentTank: Tank)

class MainScene(
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
    override val camera: Camera = PerspectiveCamera(75, window.aspectRatio, 0.1, 1000).apply {
        position.z = 150.0
        position.y = 120.0
        lookAt(Vector3(y = 100))
    }

    override val _children: HashMap<String, GameObject> by lazy {
        hashMapOf(
            "ground" to ground,
            "background" to Background()
        ).apply {
            for (tank in tanks) {
                set(tank.name, tank)
            }
        }
    }

    private val aimingAidPlane = Mesh(PlaneGeometry(500, 500), MeshPhongMaterial().apply { visible = false })
    private val aimingCaster = Raycaster()
    private var aimIntersection = Vector3()
    private var aiming = true

    private var freeCamera = true

    private val ground = Ground(offset = Vector3(x = -225, y = 40), pointDistance = 3.0)
    private val groundRenderable = ground.renderable

    private val deadTanks = ArrayDeque<Tank>()

    private val tanks = MutableList(numPlayers) {
        val data = tankColors[it]
        Tank(
            color = data.second,
            startPosition = Vector3(x = (200 / (numPlayers - 1)) * it - 100, z = 1.5),
            name = data.first
        ).apply { deadTanks.addFirst(this) }
    }

    private var currentTankIndex: Int = 0
    private val currentTank get() = tanks[currentTankIndex]

    private var currentBulletObject: BulletObject? = null

    var currentState: GameState by Delegates.observable(GameState(currentTank), ::notifyObservers)

    private val topBar = MainTopBar(this)

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
                val distance = aimIntersection.distanceTo(currentTank.position)
                currentBulletObject = currentTank.shoot(distance.toDouble() / 2)
                currentBulletObject!!.let {
                    _children["bullet"] = it
                    scene.add(it.renderable)
                    it.addImpactListener { v ->
                        onImpact()
                    }
                }
            }
        }
        window.addEventListener("pointermove", pointerMove)

        Game.instance.gameContainer.addEventListener("click", clickListener)
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
            for (tank in aliveTanks) { deadTanks.addFirst(tank) }
            Game.instance.currentScene = GameEndScene(deadTanks)
        } else {
            do {
                currentTankIndex = (currentTankIndex + 1) % tanks.size
            } while (!currentTank.active)
        }
        currentState = currentState.copy(currentTank = currentTank)
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
    }

    override suspend fun loadAssets() {
        super.loadAssets()
        topBar.initAssets()
        scene.background = Color(0x64DBFF)
        for (tank in tanks) {
            tank.adjustTransformToGround(ground)
        }
        centerCamera()
    }

    override fun update(delta: Double) {
        super.update(delta)
        val currentTank = tanks.getOrNull(currentTankIndex)
        EventManager.keyboardEvents.events.forEach {
            if (currentBulletObject != null) return@forEach
            when (it.keyCode) {
                97 -> {
                    currentTank?.move(Vector3(-6, 0, 0), delta, ground)
                }
                100 -> {
                    currentTank?.move(Vector3(6, 0, 0), delta, ground)
                }
                67 -> {
                    freeCamera = true
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
    }
}