package scenes

import core.GameObject
import ext.enableShadows
import kotlinx.coroutines.*
import three.js.Camera
import three.js.Scene
import util.Log
import util.ResourceManager
import util.await
import util.launch

sealed class GameScene {

    open val scene: Scene = Scene()
    open val camera: Camera = Camera()

    abstract val objects: Collection<GameObject>
    open suspend fun loadAssets() {
        Log.d("GameScene", objects.toString())
        objects.forEach { it.initVisual() }
    }
    abstract fun update(delta: Double)
}