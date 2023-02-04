package scenes

import core.gameobjects.GameObject
import generated.ObjectConfiguration
import generated.ObjectConfigurationItem
import generated.Pos
import generated.Rot
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import three.js.Camera
import three.js.Object3D
import three.js.Scene
import three.js.Vector3
import ui.UIController
import util.GUIHelper
import kotlin.collections.ArrayList
import kotlin.collections.map
import kotlin.collections.set

abstract class GameScene: GameObject() {

    open val scene: Scene = Scene()
    override val renderable: Object3D?
        get() = scene

    open val camera: Camera = Camera()

    protected fun addGameObject(gameObject: GameObject, name: String? = null) {
        children[name ?: gameObject.uuid.toString()] = gameObject
        MainScope().launch {
            gameObject.initVisual()
            gameObject.renderable?.let { scene.add(it) }
        }
    }

    fun getConfiguration(): ObjectConfiguration = ObjectConfiguration(
        ArrayList(children.map {
            val pos = it.value.position
            val rot = it.value.renderable?.rotation?.toVector3() ?: Vector3()
            ObjectConfigurationItem(
                name = it.key,
                pos = Pos(pos.x, pos.y, pos.z),
                rot = Rot(rot.x, rot.y, rot.z)
            )
        })
    )

    fun applyConfiguration(configuration: ObjectConfiguration) {
        for (conf in configuration.items) {
            children[conf.name]?.let {
                it.position.set(conf.pos.x, conf.pos.y, conf.pos.z)
                it.renderable?.rotation?.set(conf.rot.x, conf.rot.y, conf.rot.z)
            }
        }
    }

    protected fun updateUI(uiController: UIController) {
        GUIHelper.resetGUI()
        MainScope().launch {
            uiController.init()
        }
    }
}