package scenes.test

import ext.aspectRatio
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import scenes.GameScene
import three.CSS2DObject
import three.js.*
import util.GUIHelper
import util.Log

class CSS2DRendererTestScene : GameScene() {
    override val camera = PerspectiveCamera(75, window.aspectRatio, 0.1, 1000).apply {
        position.z = 4.0
    }

    private val cube = Mesh(BoxGeometry(1, 1, 1), MeshLambertMaterial().apply {
        emissive = Color(0x00ff00)
        emissiveIntensity = 2
        color = Color(0x00aa00)
    })

    override val scene: Scene = Scene().apply {
        add(cube)
        add(AmbientLight(color = Color(0xffffff)))
        add(CSS2DObject(
            document.createElement("test").apply {
                textContent = "Hello World! "
            }
        ).apply {
            Log.d("CSS2DObject", this.isCSS2DObject)
        })
    }

    override fun onSceneReady() {
        super.onSceneReady()
        MainScope().launch {
            GUIHelper.resetGUI()
        }
    }
}
