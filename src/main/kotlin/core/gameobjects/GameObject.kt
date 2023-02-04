package core.gameobjects

import ext.toFloatArray
import three.js.Object3D
import three.js.Vector3
import util.Uuid

abstract class GameObject {
    abstract val renderable: Object3D?

    /** A unique Identifier for the GameObject */
    val uuid = Uuid.next()

    /** [MutableMap] containing the relevant GameObjects */
    open val children: MutableMap<String, GameObject> = hashMapOf()
    // open val children: MutableSet<GameObject> = hashSetOf()

    open var position: Vector3
        get() = renderable?.position ?: Vector3()
        set(value) {
            renderable?.position?.set(*value.toFloatArray())
        }

    /** Indicates whether the object should longer be a part of the scene */
    var alive = true

    /** Function used for preparing the GameObject. Useful for e.g. loading Assets */
    open suspend fun initVisual() {}

    override fun equals(other: Any?): Boolean = other is GameObject && other.uuid == uuid
    override fun hashCode(): Int = uuid.toInt()

    /** Used for loading things that are crucial to the scene. The scene will be shown after this completed */
    open suspend fun preloadAssets() {
        children.values.forEach { it.initVisual() }
    }

    /** Called right after the scene starts being rendered */
    open suspend fun loadAssets() {}

    /** Gets called on each frame. [delta] indicates the time passed since the last frame in seconds */
    open fun update(delta: Double) {
        children.values.forEach { it.update(delta) }
    }
}