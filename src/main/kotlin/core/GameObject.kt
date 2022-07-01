package core

import three.js.Object3D

abstract class GameObject {
    abstract val renderable: Object3D?

    abstract suspend fun initVisual()

    open fun update(deltaTime: Float) {}
}