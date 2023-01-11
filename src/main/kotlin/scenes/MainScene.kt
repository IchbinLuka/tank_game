package scenes

import core.Game
import core.gameobjects.GameObject
import ext.*
import gameobjects.Background
import gameobjects.Ground
import gameobjects.Tank
import gameobjects.particles.Explosion
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Element
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.pointerevents.PointerEvent
import three.js.*
import util.*
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

data class GameState(val currentTank: Tank)

class MainScene : GameScene(), Observable<GameState> by DefaultObservable() {

    override val scene: Scene = Scene().apply {
        background = Color(0x3333ff)
        val light = DirectionalLight(0xFDFFD9, 1).apply {
            position.set(-30, 70, 40)
            castShadow = true
            enableShadows()
        }
        // add(DirectionalLightHelper(light, size = 10, color = 0xFDFFD9))
        // fog = FogExp2(0x5f9be5, 0.005)
        //add(AmbientLight(0x404040, 0.1))
        add(HemisphereLight(skyColor = 0x47C4F3, groundColor = 0xABAF9E, intensity = 0.5).apply {})
    }
    override val camera: Camera = PerspectiveCamera(75, window.aspectRatio, 0.1, 1000).apply {
        position.z = 100.0
        position.y = 30.0
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

    private val ground = Ground(offset = Vector3(x = -100), pointDistance = 3.0)
    private val groundRenderable = ground.renderable

    private val tanks = listOf<Tank>(
        Tank(startPosition = Vector3(x = 20, z = 1.5)),
        Tank(color = Color(0xfc522f), startPosition = Vector3(x = -20, z = 1.5))
    )



    private var currentTankIndex: Int = 0
    private val currentTank get() = tanks[currentTankIndex]

    var currentState: GameState by Delegates.observable(GameState(currentTank), notifyObservers)

    private var awaitingShot = false

    init {
        initListeners()
        for (tank in tanks) {
            tank.adjustTransformToGround(ground)
        }
    }

    private fun initListeners() {
        window.addEventListener("pointermove", {
            it as PointerEvent
            val aimingPoint = Vector2()
            val rect = (it.target as Element).getBoundingClientRect()
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
                    camera.position.x = aimingPoint.x * 200
                    camera.lookAt(Vector3())
                    currentTank.aim(Vector2(aimIntersection.x, aimIntersection.y))
                }
            }
        })
        window.addEventListener("click", { event ->
            event as MouseEvent
            if (!awaitingShot) {
                val distance = aimIntersection.distanceTo(currentTank.position)
                awaitingShot = true
                val bullet = currentTank.shoot(distance.toDouble() / 2, ground) { onImpact(it) }
                bullet?.run {
                    objects.add(bullet)
                    scene.add(bullet.renderable)
                }
            }
        })
    }

    private fun onImpact(vector3: Vector3) {
        for (tank in tanks) {
            tank.adjustTransformToGround(ground)
        }
        addGameObject(Explosion(position = vector3))
        awaitingShot = false
        currentTankIndex = (currentTankIndex + 1) % tanks.size
    }

    override suspend fun preloadAssets() {
        super.preloadAssets()
        scene.add(ResourceManager.testLandscape.await().scene.apply {
            enableShadows()
            applyCustomMat(MeshToonMaterial().apply { color = Color(0x195917) })
        })
        objects.forEach { o -> o.renderable?.let { scene.add(it) } }
        scene.add(groundRenderable, aimingAidPlane)
    }

    override suspend fun loadAssets() {
        super.loadAssets()
        GUIHelper.loadTopBar("tool_bar")
        scene.background = Color(0x64DBFF)
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
            if (awaitingShot) return@forEach
            when (it.keyCode) {
                97 -> {
                    currentTank?.move(Vector3(-3, 0, 0), delta, ground)
                }
                100 -> {
                    Log.d(TAG, camera.rotation.toVector3().toFloatList())
                    currentTank?.move(Vector3(3, 0, 0), delta, ground)
                }
            }
        }
    }
}