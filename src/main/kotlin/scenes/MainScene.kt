package scenes

import core.GameObject
import core.GameScene
import ext.aspectRatio
import gameobjects.TestObject
import kotlinx.browser.window
import three.js.Camera
import three.js.PerspectiveCamera
import three.js.Scene

class MainScene : GameScene() {

    override val scene: Scene = Scene()
    override val camera: Camera = PerspectiveCamera(75, window.aspectRatio, 0.1, 1000).apply {
        position.z = 5
    }

    private val objects: MutableList<GameObject> = mutableListOf()

    init {
        objects.add(TestObject())
    }
    override fun update(delta: Double) {

    }
}