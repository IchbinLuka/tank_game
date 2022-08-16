package scenes

import core.gameobjects.GameObject
import ext.aspectRatio
import ext.enableShadows
import gameobjects.Ground
import gameobjects.Tank
import kotlinx.browser.window
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.pointerevents.PointerEvent
import three.js.*
import util.*

class MainScene : GameScene() {

    override val scene: Scene = Scene().apply {
        background = Color(0x3333ff)
        val light = DirectionalLight(0xFDFFD9, 1).apply {
            position.set(-30, 70, 40)
            castShadow = true
            enableShadows()
        }
        add(light)
        add(DirectionalLightHelper(light, size = 10, color = 0xFDFFD9))
        fog = FogExp2(0x5f9be5, 0.005)
        //add(AmbientLight(0x404040, 0.1))
        add(HemisphereLight(skyColor = 0x47C4F3, groundColor = 0xABAF9E, intensity = 0.5).apply {})
    }
    override val camera: Camera = PerspectiveCamera(75, window.aspectRatio, 0.1, 1000).apply {
        position.z = 50
        position.y = 3
        //rotation.x = 0.3
    }

    override val objects: MutableList<GameObject> by lazy {
        mutableListOf<GameObject>().apply {
            addAll(tanks)
        }
    }

    private val aimingAidPlane = Mesh(PlaneGeometry(500, 500), MeshPhongMaterial().apply { visible = false })
    private val aimingCaster = Raycaster()
    private val aimingPoint = Vector2()
    private var aiming = true

    private val ground = Ground(offset = Vector3(-100, -20, 4), res = 50, depth = 8.0)
    private val groundRenderable = ground.renderable

    private val tanks = listOf<Tank>(
        Tank()
    )

    private var currentTankIndex: Int = 0
    private val currentTank get() = tanks[currentTankIndex]

    private var awaitingShot = false

    init {
        window.addEventListener("pointermove", {
            it as PointerEvent
            aimingPoint.x = ( it.clientX.toFloat() / window.innerWidth ) * 2 - 1
            aimingPoint.y = - ( it.clientY.toFloat() / window.innerHeight ) * 2 + 1
        })
        window.addEventListener("click", {
            it as MouseEvent
            if (!awaitingShot) {
                val bullet = currentTank.shoot(25.0, ground)
                bullet?.run {
                    objects.add(bullet)
                    scene.add(bullet.renderable)
                }
            }
        })
    }

    override suspend fun preloadAssets() {
        super.preloadAssets()
        scene.add(ResourceManager.testLandscape.await().scene.apply {
            enableShadows()
        })
        objects.forEach { o -> o.renderable?.let { scene.add(it) } }
        scene.add(aimingAidPlane)
        scene.add(groundRenderable)
    }

    override suspend fun loadAssets() {
        super.loadAssets()
        scene.background = ResourceManager.sunnySky.await()
    }

    override fun update(delta: Double) {
        objects.forEachIndexed {
            i, o ->
            o.update(delta)
            if (!o.alive) {
                objects.removeAt(i) // TODO: Improve this inefficient approach
            }
        }
        val currentTank = tanks.getOrNull(currentTankIndex)
        EventManager.keyboardEvents.events.forEach {
            when(it.keyCode) {
                97  -> {
                    currentTank?.move(Vector3(-3, 0, 0), delta, ground)
                }
                100 -> {
                    currentTank?.move(Vector3(3, 0, 0), delta, ground)
                }
            }
        }
        if (aiming) {
            aimingCaster.setFromCamera(aimingPoint, camera)
            val intersection = aimingCaster.intersectObject(aimingAidPlane)
            if (intersection.isNotEmpty()) {
                val point = intersection.first().point
                currentTank?.aim(Vector2(point.x, point.y))
            }
        }
    }
}