package core

import kotlinx.browser.document
import kotlinx.browser.window
import scenes.MainScene
import three.OrbitControls
import three.js.*
import util.EventManager
import util.GUIHelper

object Game {
    private val clock = Clock()
    private var currentScene_: GameScene = MainScene()
    var currentScene: GameScene
    get() = currentScene_
    set(value) {
        GUIHelper.resetGUI()
        currentScene_ = value
    }

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
                currentScene.camera.asDynamic().aspect = window.innerWidth / window.innerHeight
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