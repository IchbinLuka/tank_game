package scenes.test

import core.gameobjects.GameObject
import ext.aspectRatio
import gameobjects.BackgroundLandscape
import kotlinx.browser.window
import scenes.GameScene
import three.js.AmbientLight
import three.js.Color
import three.js.PerspectiveCamera
import three.js.Scene

class BackgroundTestScene : GameScene() {
    override val camera = PerspectiveCamera(75, window.aspectRatio, 0.1, 1000).apply {
        position.z = 20.0
    }

    override val initialChildren: Collection<GameObject> = listOf(BackgroundLandscape())

    override val scene = Scene().apply {
        add(AmbientLight(color = Color(0xffffff)))
        background = Color(0x99f0fc)
    }
}