package scenes

import core.GameObject
import three.js.Camera
import three.js.Scene
import util.Log

sealed class GameScene {

    open val scene: Scene = Scene()
    open val camera: Camera = Camera()

    abstract val objects: Collection<GameObject>
    open suspend fun preloadAssets() {
        Log.d("GameScene", objects.toString())
        objects.forEach { it.initVisual() }
    }

    open suspend fun loadAssets() {}
    abstract fun update(delta: Double)
}