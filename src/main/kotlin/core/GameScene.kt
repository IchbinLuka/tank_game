package core

import three.js.Scene

abstract class GameScene {

    abstract val scene: Scene

    abstract fun update()
}