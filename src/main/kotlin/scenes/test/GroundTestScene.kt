package scenes.test

import core.gameobjects.GameObject
import ext.aspectRatio
import gameobjects.Ground
import kotlinx.browser.window
import scenes.GameScene
import three.js.*

class GroundTestScene: GameScene() {
    override val objects: Collection<GameObject> = listOf(
        Ground(width = 50, depth = 15)
    )

    override val camera: Camera = PerspectiveCamera(75, window.aspectRatio, 0.1, 1000).apply {
        position.z = 4
    }

    override suspend fun preloadAssets() {
        super.preloadAssets()
        scene.apply {
            add(AmbientLight(color = Color(0xffffff)))
            add(DirectionalLight(color = Color(0xffffff), intensity = 2).apply {
                position.apply {
                    x = 50
                    y = 20
                }
                rotation.apply {
                    x = 10
                    y = 10
                    z = 10
                }
            })
            objects.forEach { it.renderable?.let { r -> add(r) } }
            background = Color(0x4ff4f7)
        }
    }

    override fun update(delta: Double) {}
}