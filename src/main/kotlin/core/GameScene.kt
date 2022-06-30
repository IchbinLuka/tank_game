package core

import three.js.Camera
import three.js.Scene

abstract class GameScene {

    abstract val scene: Scene
    abstract val camera: Camera

    abstract fun update(delta: Float)
}