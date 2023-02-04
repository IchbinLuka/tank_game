package scenes.test

import core.gameobjects.GameObject
import ext.aspectRatio
import gameobjects.Ground
import kotlinx.browser.window
import scenes.GameScene
import three.js.*

class GroundTestScene: GameScene() {
    override val _children: MutableMap<String, GameObject> = hashMapOf(
        "ground" to Ground()
    )

    override val camera: Camera = PerspectiveCamera(75, window.aspectRatio, 0.1, 1000).apply {
        position.z = 4.0
    }

    override suspend fun preloadAssets() {
        super.preloadAssets()
        scene.apply {
            add(AmbientLight(color = Color(0xffffff)))
            add(DirectionalLight(color = Color(0xffffff), intensity = 2).apply {
                position.apply {
                    x = 50.0
                    y = 20.0
                }
                rotation.apply {
                    x = 10
                    y = 10
                    z = 10
                }
            })
            this@GroundTestScene._children.forEach { it.value.renderable?.let { r -> add(r) } }
            background = Color(0x4ff4f7)
        }
    }

    override fun update(delta: Double) {}
}