package core

import kotlinx.browser.document
import kotlinx.browser.window
import scenes.ParticleTestScene
import stats.js.Stats
import three.OrbitControls
import three.js.*
import three.postprocessing.EffectComposer
import three.postprocessing.RenderPass
import three.postprocessing.SSAOPass
import three.postprocessing.UnrealBloomPass
import util.EventManager
import util.GUIHelper
import util.Log

object Game {
    private val clock = Clock()
    private var currentScene_: GameScene = ParticleTestScene()
    var currentScene: GameScene
    get() = currentScene_
    set(value) {
        GUIHelper.resetGUI()
        currentScene_ = value
    }

    private val stats = Stats().apply {
        showPanel(0) // 0: fps, 1: ms, 2: mb, 3+: custom
        document.body?.appendChild(domElement)
        with (domElement.style) {
            position="fixed"
            top="0px"
            left="0px"
        }
    }

    private val renderer = WebGLRenderer(object : WebGLRendererParameters {
        override var antialias: Boolean? = true
    }).apply {
        document.body?.appendChild(domElement)
        //shadowMap.enabled = true
        setSize(window.innerWidth, window.innerHeight)
        setPixelRatio(window.devicePixelRatio)
    }

    private val composer: EffectComposer = EffectComposer(renderer)

    init {
        window.onloadend = {
            renderer.domElement.focus()
        }
        stats.begin()
        val width = window.innerWidth
        val height = window.innerHeight
        OrbitControls(currentScene.camera, renderer.domElement)
        //renderer.toneMappingExposure = 1
        val bloomPass = UnrealBloomPass(Vector2( window.innerWidth, window.innerHeight ), 0.75, 0.2, 0.3)
        val ssaoPass = SSAOPass(currentScene.scene, currentScene.camera, width, height).apply {
            kernelRadius = 5
            maxDistance = 50
        }
        val renderPass = RenderPass(currentScene.scene, currentScene.camera)
        composer.apply {
            addPass(renderPass)
            //addPass(ssaoPass)
            addPass(bloomPass)
        }
        window.onresize = {
            renderer.apply {
                setSize(window.innerWidth, window.innerHeight)
                setPixelRatio(window.devicePixelRatio)
                currentScene.camera.asDynamic().aspect = window.innerWidth / window.innerHeight
            }
        }
    }

    fun mainLoop() {
        stats.begin()
        val delta = clock.getDelta().toDouble()
        EventManager.keyboardEvents.switchBatches()
        window.requestAnimationFrame { mainLoop() }
        currentScene.update(delta)
        composer.render()
        stats.update()
    }
}