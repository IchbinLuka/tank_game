package core

import kotlinx.browser.window
import scenes.MainScene
import three.js.Clock

object Game {
    private val clock = Clock()
    var currentScene: GameScene = MainScene()


    fun mainLoop() {
        val delta = clock.getDelta().toDouble()

        window.requestAnimationFrame { mainLoop() }
    }
}