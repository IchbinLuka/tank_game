package gameobjects

import Cube
import core.GameObject
import three.js.Mesh
import three.js.MeshPhongMaterial
import three.js.Object3D

class TestObject : GameObject() {
    override var visual: Object3D = Mesh(Cube(), MeshPhongMaterial())

    override fun update() {

    }
}