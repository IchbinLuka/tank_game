package core

import DevTools
import core.models.Settings
import ext.TAG
import ext.aspectRatio
import ext.tryCast
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.*
import org.w3c.dom.HTMLCanvasElement
import scenes.GameScene
import scenes.LoadingScene
import stats.js.Stats
import three.OrbitControls
import three.js.*
import three.postprocessing.*
import util.*
import kotlin.coroutines.CoroutineContext

val currentScene: GameScene
    get() = Game.instance.currentScene

class Game(
    initialScene: GameScene
) : CoroutineScope {

    override val coroutineContext: CoroutineContext = MainScope().coroutineContext

    val settings: ValueNotifier<Settings> = ValueNotifier(Settings.fromCookies() ?: Settings()).apply {
        addListener { it.saveToCookies() }
    }

    val gameContainer: HTMLCanvasElement
        get() = document.getElementById("gameContainer") as HTMLCanvasElement

    private var _currentScene: GameScene? = null
    var currentScene: GameScene
        get() = _currentScene!!
        set(value) {
            _currentScene?.dispose()
            _currentScene = LoadingScene()
            launch { _currentScene!!.loadAssets() }
            updateComposer()
            launch(Dispatchers.Unconfined) {
                Localizations.ensureInitialized()
                value.preloadAssets()
                GUIHelper.resetGUI()
                _currentScene = value
                _currentScene!!.onSceneReady()
                // _currentScene!!.registerMultiplayerSynchronized(connectionManager)
                updateComposer()
                _currentScene!!.loadAssets()
                updateRenderer()
            }
        }

    private val stats = Stats().apply {
        showPanel(0) // 0: fps, 1: ms, 2: mb, 3+: custom
        domElement.id = "fpsDisplay"
        document.body?.appendChild(domElement)
        with(domElement.style) {
            visibility = "hidden"
            position = "fixed"
            top = "0px"
            left = "0px"
        }
    }

    private val renderer = WebGLRenderer(object : WebGLRendererParameters {
        override var antialias: Boolean? = true
        override var canvas: dynamic = document.getElementById("gameContainer")!!
    })

    private suspend fun ensureLocalizationsInitialized() {
        Localizations.load(settings.value.language)
    }

    private fun updateRenderer() {
        val topBar = document.getElementById("topBar")!!
        renderer.setPixelRatio(window.devicePixelRatio)
        _currentScene?.camera?.tryCast<PerspectiveCamera>()?.let {
            it.aspect = window.aspectRatio
            it.updateProjectionMatrix()
        }
        renderer.setSize(window.innerWidth, window.innerHeight)
    }

    private var composer: EffectComposer = EffectComposer(OutlineEffect(renderer))
    private fun updateComposer() {
        composer = EffectComposer(renderer)
        val width = window.innerWidth
        val height = window.innerHeight
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


    fun activateDeveloperCamera() {
        OrbitControls(currentScene.camera, renderer.domElement)
    }

    init {
        window.addEventListener("resize", { updateRenderer() }, false)
        window.onloadend = {
            renderer.domElement.focus()
        }
        stats.begin()
        currentScene = initialScene
        _instance = this
    }

    private var running = true
    fun mainLoop() = launch {
        val effect = OutlineEffect(renderer)
        val clock = Clock()
        while (running) {
            val delta = clock.getDelta().toDouble()
            stats.begin()
            EventManager.keyboardEvents.switchBatches()
            currentScene.preUpdate()
            currentScene.update(delta * DevTools.gameSpeed)
            currentScene.lateUpdate()
            effect.render(currentScene.scene, currentScene.camera)
            stats.update()
            val error = renderer.getContext().getError()
            if (error != 0) {
                Log.e(TAG, error.toString())
            }
            window.awaitAnimationFrame()
        }
    }

    companion object {
        val instance: Game
            get() = _instance!!
        var _instance: Game? = null
    }
}