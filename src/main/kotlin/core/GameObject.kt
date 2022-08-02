package core

import three.js.Object3D

abstract class GameObject {
    abstract val renderable: Object3D?

    open suspend fun initVisual() {}

    open fun update(deltaTime: Double) {}
}