package scenes

import core.Game
import core.gameobjects.GameObject
import ext.applyCustomMat
import ext.aspectRatio
import ext.enableShadows
import ext.fromId
import gameobjects.Background
import gameobjects.Tank
import kotlinx.browser.window
import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import three.js.*
import util.GUIHelper
import util.ResourceManager

class StartScene: GameScene() {
    override val objects: HashSet<GameObject> = hashSetOf(
        Background(offset = Vector3(z = -150, y = -115))
    )

    override val scene: Scene = Scene().apply {
        background = Color(0x3333ff)
        val light = DirectionalLight(0xFDFFD9, 1).apply {
            position.set(-30, 70, 40)
            castShadow = true
            enableShadows()
        }
        background = Color(0x64DBFF)
        add(light)
        add(AmbientLight(0x404040, 0.1))
        add(HemisphereLight(skyColor = 0x47C4F3, groundColor = 0xABAF9E, intensity = 0.5))
    }
    override val camera: Camera = PerspectiveCamera(60, window.aspectRatio, 0.1, 1000).apply {
        position.x = -1.6
        position.y = 8.0
        position.z = 36.0

        rotation.x = -0.4
    }

    override suspend fun preloadAssets() {
        val tankColors = hashMapOf<String, Color>(
            "tank1" to Color(0x338530),
            "tank2" to Color(0xfc522f),
            "tank3" to Color(0xef3434)
        )

        val landscape = ResourceManager.startLandScape.await().scene
        scene.add(landscape)
        landscape.traverse {
            if (tankColors.containsKey(it.name)) {
                objects.add(
                    Tank(color = tankColors[it.name]!!, startPosition = it.position, startRotation = it.rotation)
                )
            }
        }
        landscape.applyCustomMat(
            MeshToonMaterial().apply { color = Color(0x11710F) },
            MeshToonMaterial().apply { color = Color(0x6D3600) },
            MeshToonMaterial().apply { color = Color(0x3e8e2c) }
        )
        super.preloadAssets()
        for (o in objects) {
            o.renderable?.let { scene.add(it) }
        }
    }

    override suspend fun loadAssets() {
        super.loadAssets()
        GUIHelper.loadGUI("start")
        initListeners()
    }

    private var playersSlider: HTMLInputElement? = null
    private var playersDisplay: Element? = null

    private fun updatePlayersDisplay() {
        playersDisplay?.innerHTML = playersSlider!!.value
    }

    private fun initListeners() {
        playersDisplay = Element.fromId("num_players_display")
        playersSlider = (Element.fromId("num_players_slider") as HTMLInputElement).apply {
            addEventListener("input", {
                updatePlayersDisplay()
            })
            max = MainScene.tankColors.size.toString()
        }

        Element.fromId("startButton")?.apply {
            addEventListener("click", {
                Game.instance.currentScene = MainScene(numPlayers = playersSlider?.value?.toIntOrNull() ?: 3)
            })
        }
        updatePlayersDisplay()
    }
}