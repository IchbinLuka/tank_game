package core

import kotlinx.browser.document
import kotlinx.browser.window
import scenes.MainScene
import three.js.Clock
import three.js.WebGLRenderer

object Game {
    private val clock = Clock()
    var currentScene: GameScene = MainScene()

    private val renderer = WebGLRenderer().apply {
        document.body?.appendChild(domElement)
        setSize(window.innerWidth, window.innerHeight)
        setPixelRatio(window.devicePixelRatio)
    }

    fun mainLoop() {
        val delta = clock.getDelta().toDouble()
        currentScene.update(delta)
        renderer.render(currentScene.scene, currentScene.camera)
        window.requestAnimationFrame { mainLoop() }
    }
}