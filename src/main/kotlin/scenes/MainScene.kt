package scenes

import core.GameObject
import core.GameScene
import ext.aspectRatio
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
        add(DirectionalLight(0xffffff, 1).apply { position.set(-1, 2, 4) })
        add(AmbientLight(0x404040, 1))
    }
    override val camera: Camera = PerspectiveCamera(75, window.aspectRatio, 0.1, 1000).apply {
        position.z = 10
    }

    private val objects: MutableList<GameObject> = mutableListOf(
        Tank()
    )

    init {
        objects.forEach { o -> o.renderable?.let { scene.add(it) } }
        launch {
            loadAssets()
        }
    }
    private suspend fun loadAssets() {
        objects.forEach { it.initVisual() }
    }
    override fun update(delta: Float) {
        objects.forEach { it.update(delta) }
    }
}