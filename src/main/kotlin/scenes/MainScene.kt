package scenes

import core.GameObject
import core.GameScene
import ext.aspectRatio
import gameobjects.TestObject
import kotlinx.browser.window
import org.w3c.dom.WorkerGlobalScope
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

    private val objects: MutableList<GameObject> = mutableListOf()

    init {
        //objects.add(TestObject())
        objects.forEach { scene.add(it.visual) }
        launch {
            loadAssets()
        }
    }
    private suspend fun loadAssets() {
        val loader = GLTFLoader()
        window.onkeypress
        console.log("Loading suzanne")
        val suzanne = ResourceManager.suzanne.await()
        console.log("Loaded Suzanne")
        scene.add(suzanne.scene)
    }
    override fun update(delta: Double) {

    }
}