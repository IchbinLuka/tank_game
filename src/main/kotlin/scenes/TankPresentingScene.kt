package scenes

import core.gameobjects.GameObject
import ext.*
import gameobjects.Background
import gameobjects.Tank
import kotlinx.browser.window
import three.js.*
import util.Log
import util.ResourceManager

abstract class TankPresentingScene(
    private val tanks: Collection<Tank>
): GameScene() {

    private val light = DirectionalLight(0xFDFFD9, 1).apply {
        position.set(-30, 70, 40)
        castShadow = true
        enableShadows()
    }

    final override val scene: Scene = Scene().apply {
        background = Color(0x3333ff)
        background = Color(0x64DBFF)
        add(light)
        add(AmbientLight(0x404040, 0.1))
        add(HemisphereLight(skyColor = 0x47C4F3, groundColor = 0xABAF9E, intensity = 0.5))
    }

    override val initialChildren: Collection<GameObject> = mutableListOf<GameObject>(
        Background(offset = Vector3(z = -150, y = -115), scene = scene, light = light),
    ).apply {
        addAll(tanks)
    }

    override val camera = PerspectiveCamera(60, window.aspectRatio, 0.1, 3000).apply {
        position.x = -1.6
        position.y = 8.0
        position.z = 36.0

        rotation.x = -0.4
    }

    override suspend fun preloadAssets() {
        super.preloadAssets()
        val landscape = ResourceManager.startLandScape.await().scene
        scene.add(landscape)
        val map = HashMap<String, GameObject>().apply {
            putAll((1..3).map { "tank$it" } zip tanks)
        }
        Log.d(TAG, map)
        landscape.placeChildren(
            map
        )
        landscape.applyCustomMat(
            MeshToonMaterial().apply { color = Color(0x11710F) },
            MeshToonMaterial().apply { color = Color(0x6D3600) },
            MeshToonMaterial().apply { color = Color(0x3e8e2c) }
        )
        for (tank in findObjects<Tank>()) {
            tank.updateFireRotation()
        }
    }

}