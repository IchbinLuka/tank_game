package scenes.test

import core.gameobjects.GameObject
import ext.aspectRatio
import kotlinx.browser.window
import scenes.GameScene
import three.js.*

class BloomTestScene: GameScene() {
    private val cube = Mesh(BoxGeometry(1,1,1), MeshLambertMaterial().apply {
        emissive = Color(0x00ff00)
        emissiveIntensity = 2
        color = Color(0x00aa00)
    })

    override val objects: Collection<GameObject>
        get() = TODO("Not yet implemented")

    override val scene: Scene = Scene().apply {
        add(cube)
        add(AmbientLight(color = Color(0xffffff)))
    }

    override val camera: Camera = PerspectiveCamera(75, window.aspectRatio, 0.1, 1000).apply {
        position.z = 4.0
    }

    override fun update(delta: Double) {}
}