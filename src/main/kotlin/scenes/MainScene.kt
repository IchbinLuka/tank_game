package scenes

import core.Game
import core.gameobjects.GameObject
import ext.*
import gameobjects.Background
import gameobjects.Bullet
import gameobjects.Ground
import gameobjects.Tank
import gameobjects.particles.Explosion
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.pointerevents.PointerEvent
import three.js.*
import ui.MainTopBar
import util.*
import kotlin.math.max
import kotlin.math.pow
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
        // add(DirectionalLightHelper(light, size = 10, color = 0xFDFFD9))
        // fog = FogExp2(0x5f9be5, 0.005)
        add(AmbientLight(0x404040, 0.1))
        add(HemisphereLight(skyColor = 0x47C4F3, groundColor = 0xABAF9E, intensity = 0.5))
    }
    override val camera: Camera = PerspectiveCamera(75, window.aspectRatio, 0.1, 1000).apply {
        position.z = 150.0
        position.y = 120.0
        rotation.x = -0.3
    }

    override val objects: MutableSet<GameObject> by lazy {
        hashSetOf<GameObject>(
            ground,
            Background()
        ).apply {
            addAll(tanks)
        }
    }

    private val aimingAidPlane = Mesh(PlaneGeometry(500, 500), MeshPhongMaterial().apply { visible = false })
    private val aimingCaster = Raycaster()
    private var aimIntersection = Vector3()
    private var aiming = true

    private var freeCamera = false

    private val ground = Ground(offset = Vector3(x = -225, y = 40), pointDistance = 3.0)
    private val groundRenderable = ground.renderable

    /*private val tanks = mutableListOf<Tank>(
        Tank(startPosition = Vector3(x = 0, z = 1.5), name = "Green"),
        Tank(color = Color(0xfc522f), startPosition = Vector3(x = -60, z = 1.5), name = "Orange"),
        Tank(color = Color(0xff0000), startPosition = Vector3(x = 60, z = 1.5), name = "Red")
    )*/

    private val tanks = MutableList(numPlayers) {
        val data = tankColors[it]
        Tank(
            color = data.second,
            startPosition = Vector3(x = (200 / (numPlayers - 1)) * it - 100, z = 1.5),
            name = data.first
        )
    }



    private var currentTankIndex: Int = 0
    private val currentTank get() = tanks[currentTankIndex]

    private var currentBullet: Bullet? = null

    var currentState: GameState by Delegates.observable(GameState(currentTank), notifyObservers)

    // private var awaitingShot = false

    private val topBar = MainTopBar(this)

    init {
        initListeners()
    }

    private fun initListeners() {
        window.addEventListener("pointermove", {
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
                        camera.position.x = aimingPoint.x * 50
                        centerCamera()
                    }
                }
            }
        })
        window.addEventListener("click", { event ->
            event as MouseEvent
            if (currentBullet == null) {
                val distance = aimIntersection.distanceTo(currentTank.position)
                currentBullet = currentTank.shoot(distance.toDouble() / 2, ground) { onImpact(it) }
                currentBullet!!.let {
                    objects.add(it)
                    scene.add(it.renderable)
                }
            }
        })
    }

    private fun centerCamera() {
        camera.lookAt(Vector3(y = 100))
    }

    private fun onImpact(vector3: Vector3) {
        val iterator = tanks.iterator()
        iterator.forEach { tank ->
            val distance = tank.position.distanceTo(vector3).toDouble()
            if (distance < currentBullet!!.explosionWidth) {
                tank.damage((1 / max(distance.pow(2) * .05, 1.0)) * currentBullet!!.damage)
            }
            Log.d(TAG, "distance: $distance")
            tank.adjustTransformToGround(ground)
        }
        addGameObject(Explosion(position = vector3))
        currentBullet = null
        nextTank()
    }

    private fun nextTank() {
        val aliveTanks = tanks.filter { it.alive }
        if (aliveTanks.size <= 1) {
            MainScope().launch {
                GUIHelper.loadGUI("game_over")
            }
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
            scale.set(Vector3(2, 2, 2))
        })
        objects.forEach { o -> o.renderable?.let { scene.add(it) } }
        scene.add(groundRenderable, aimingAidPlane)
        centerCamera()
    }

    override suspend fun loadAssets() {
        super.loadAssets()
        topBar.init()
        scene.background = Color(0x64DBFF)
        for (tank in tanks) {
            tank.adjustTransformToGround(ground)
        }
    }

    override fun update(delta: Double) {
        val iterator = objects.iterator()
        for (obj in iterator) {
            obj.update(delta)
            if (!obj.alive) {
                iterator.remove() // Remove items via the iterator to avoid a [ConcurrentModificationException]
                obj.renderable?.let { scene.remove(it) }
            }
        }
        val currentTank = tanks.getOrNull(currentTankIndex)
        EventManager.keyboardEvents.events.forEach {
            if (currentBullet != null) return@forEach
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