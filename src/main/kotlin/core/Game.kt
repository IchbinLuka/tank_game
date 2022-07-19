package core

import kotlinx.browser.document
import kotlinx.browser.window
import scenes.MainScene
import scenes.MeshTestScene
import three.OrbitControls
import three.js.*
import util.EventManager

object Game {
    private val clock = Clock()
    var currentScene: GameScene = MainScene()

    private val renderer = WebGLRenderer(object : WebGLRendererParameters {
        override var antialias: Boolean? = true
    }).apply {
        document.body?.appendChild(domElement)
        //shadowMap.enabled = true
        setSize(window.innerWidth, window.innerHeight)
        setPixelRatio(window.devicePixelRatio)
    }

    init {
        val controls = OrbitControls(currentScene.camera, renderer.domElement)
        window.onresize = {
            renderer.apply {
                setSize(window.innerWidth, window.innerHeight)
                setPixelRatio(window.devicePixelRatio)
            }
        }
    }

    fun mainLoop() {
        val delta = clock.getDelta().toFloat()
        EventManager.keyboardEvents.switchBatches()
        currentScene.update(delta)
        renderer.render(currentScene.scene, currentScene.camera)
        window.requestAnimationFrame { mainLoop() }
    }
}