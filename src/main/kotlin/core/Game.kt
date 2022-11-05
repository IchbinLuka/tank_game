package core

import ext.TAG
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.*
import scenes.GameScene
import scenes.LoadingScene
import stats.js.Stats
import three.OrbitControls
import three.js.Clock
import three.js.Vector2
import three.js.WebGLRenderer
import three.js.WebGLRendererParameters
import three.postprocessing.*
import util.EventManager
import util.GUIHelper
import util.Log
import kotlin.coroutines.CoroutineContext

class Game(
    initialScene: GameScene
) : CoroutineScope {

    override val coroutineContext: CoroutineContext = MainScope().coroutineContext

    private val clock = Clock()
    private lateinit var currentScene_: GameScene
    var currentScene: GameScene
        get() = currentScene_
        set(value) {
            currentScene_ = LoadingScene()
            launch { currentScene_.loadAssets() }
            updateComposer()
            launch(Dispatchers.Unconfined) {
                value.preloadAssets()
                GUIHelper.resetGUI()
                currentScene_ = value
                updateComposer()
                currentScene_.loadAssets()
            }
            Log.d("Game", "Loading assets")
        }

    private val stats = Stats().apply {
        showPanel(0) // 0: fps, 1: ms, 2: mb, 3+: custom
        document.body?.appendChild(domElement)
        with(domElement.style) {
            position = "fixed"
            top = "0px"
            left = "0px"
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

    private var composer: EffectComposer = EffectComposer(OutlineEffect(renderer))
    private fun updateComposer() {
        composer = EffectComposer(renderer)
        val width = window.innerWidth
        val height = window.innerHeight
        OrbitControls(currentScene.camera, renderer.domElement)
        //renderer.toneMappingExposure = 1
        val bloomPass = UnrealBloomPass(Vector2(window.innerWidth, window.innerHeight), 0.75, 0.2, 0.3)
        val ssaoPass = SSAOPass(currentScene.scene, currentScene.camera, width, height).apply {
            kernelRadius = 5
            maxDistance = 50
        }
        val renderPass = RenderPass(currentScene.scene, currentScene.camera)
        composer.apply {
            addPass(renderPass)
            //addPass(ssaoPass)
            //addPass(bloomPass)
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
        val effect = OutlineEffect(renderer)
        val clock = Clock()
        while (running) {
            val delta = clock.getDelta().toDouble()
            stats.begin()
            EventManager.keyboardEvents.switchBatches()
            currentScene.update(delta)
            // composer.render()
            effect.render(currentScene_.scene, currentScene_.camera)
            stats.update()
            val error = renderer.getContext().getError()
            if (error != 0) {
                Log.e(TAG, error.toString())
            }
            window.awaitAnimationFrame()
        }
    }
}