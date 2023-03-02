package gameobjects

import core.gameobjects.GameObject
import three.js.Group
import three.js.Object3D

class StartLandscape: GameObject() {
    override val renderable: Object3D = Group()


}