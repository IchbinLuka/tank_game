package scenes

import core.gameobjects.GameObject
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import three.js.Camera
import three.js.Scene
import util.Log

abstract class GameScene {

    open val scene: Scene = Scene()
    open val camera: Camera = Camera()

    /** [MutableSet] containing the relevant GameObjects */
    abstract val objects: MutableSet<GameObject>

    protected fun addGameObject(gameObject: GameObject) {
        objects.add(gameObject)
        MainScope().launch {
            gameObject.initVisual()
            gameObject.renderable?.let { scene.add(it) }
        }
    }

    /** Used for loading things that are crucial to the scene. The scene will be shown after this completed */
    open suspend fun preloadAssets() {
        Log.d("GameScene", objects.toString())
        objects.forEach { it.initVisual() }
    }

    /** Called right after the scene starts being rendered */
    open suspend fun loadAssets() {}
    open fun update(delta: Double) {
        objects.forEach { it.update(delta) }
    }
}