package core

import ext.TAG
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.*
import scenes.GameScene
import scenes.LoadingScene
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
import util.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class Game(
    initialScene: GameScene
): CoroutineScope {

    override val coroutineContext: CoroutineContext = MainScope().coroutineContext

    private val clock = Clock()
    private lateinit var currentScene_: GameScene
    var currentScene: GameScene
    get() = currentScene_
    set(value) {
        currentScene_ = LoadingScene()
        updateComposer()
        launch {
            value.loadAssets()
            GUIHelper.resetGUI()
            currentScene_ = value
            updateComposer()
        }
        Log.d("Game", "Loading assets")
    }

    companion object {
        private var instance_: Game? = null
        val instance: Game get() {
            if (instance_ == null) {
                throw IllegalStateException("No instance intialized")
            }
            return instance_!!
        }
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

    private var composer: EffectComposer = EffectComposer(renderer)
    private fun updateComposer() {
        composer = EffectComposer(renderer)
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
    }

    init {
        window.onloadend = {
            renderer.domElement.focus()
        }
        stats.begin()
        currentScene = initialScene
        window.onresize = {
            renderer.apply {
                setSize(window.innerWidth, window.innerHeight)
                setPixelRatio(window.devicePixelRatio)
                currentScene.camera.asDynamic().aspect = window.innerWidth / window.innerHeight
            }
        }
    }
    private var running = true
    fun mainLoop() = launch {
        var delta = window.performance.now()
        while (running) {
            stats.begin()
            EventManager.keyboardEvents.switchBatches()
            currentScene.update(delta)
            composer.render()
            stats.update()
            delta = window.awaitAnimationFrame()
        }
    }
}