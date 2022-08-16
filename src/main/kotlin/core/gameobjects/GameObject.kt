package core.gameobjects

import ext.toFloatArray
import three.js.Object3D
import three.js.Vector3

abstract class GameObject {
    abstract val renderable: Object3D?

    open var position: Vector3
    get() = renderable?.position ?: TODO()
    set(value) { renderable?.position?.set(*value.toFloatArray()) }

    /** Indicates whether the object should longer be a part of the scene */
    var alive = true

    /** Function used for preparing the GameObject. Useful for e.g. loading Assets */
    open suspend fun initVisual() {}

    /** Gets called on each frame. [deltaTime] indicates the time passed since the last frame in seconds */
    open fun update(deltaTime: Double) {}
}