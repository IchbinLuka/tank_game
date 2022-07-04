package scenes

import core.GameObject
import core.GameScene
import ext.aspectRatio
import ext.plus
import extensions.enableShadows
import gameobjects.Ground
import gameobjects.Tank
import kotlinx.browser.window
import three.GLTFLoader
import three.js.*
import util.ResourceManager
import util.await
import util.launch

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

    private val objects: MutableList<GameObject> = mutableListOf(
        Tank(),
        Ground()
    )

    init {
        launch {
            loadAssets()
            objects.forEach { o -> o.renderable?.let { scene.add(it) } }
        }
    }
    private suspend fun loadAssets() {
        objects.forEach { it.initVisual() }
        scene.add(ResourceManager.testLandscape.await().scene.apply {
            enableShadows()
        })
        ResourceManager.sunnySky.then {
            scene.background = it
        }
    }
    override fun update(delta: Float) {
        objects.forEach { it.update(delta) }
    }
}