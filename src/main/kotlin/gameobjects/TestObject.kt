package gameobjects

import Cube
import core.GameObject
import three.js.*

class TestObject : GameObject() {
    override var visual: Object3D = Mesh(BoxGeometry(1,1,1), MeshPhongMaterial().apply { color = Color(0xffffff) })

    override fun update() {
        // val loader = ObjectLoader()

    }
}