package core

import three.js.Object3D

abstract class GameObject {
    abstract var visual: Object3D

    abstract fun update()
}