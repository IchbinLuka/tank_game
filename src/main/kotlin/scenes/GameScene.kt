package scenes

import core.gameobjects.GameObject
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import three.js.Camera
import three.js.Object3D
import three.js.Scene
import ui.UIController
import util.GUIHelper

abstract class GameScene: GameObject() {

    open val scene: Scene = Scene()
    override val renderable: Object3D?
        get() = scene

    open val camera: Camera = Camera()

    protected fun addGameObject(gameObject: GameObject) {
        children.add(gameObject)
        MainScope().launch {
            gameObject.initVisual()
            gameObject.renderable?.let { scene.add(it) }
        }
    }



    protected fun updateUI(uiController: UIController) {
        GUIHelper.resetGUI()
        MainScope().launch {
            uiController.init()
        }
    }
}