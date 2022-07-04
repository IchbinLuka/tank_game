package gameobjects

import Cube
import core.GameObject
import extensions.enableShadows
import three.js.*

class Ground(
    val res: Int = 200,
    val width: Float = 200f,
    val offset: Vector3 = Vector3(0, 0, 0),
    val depth: Float = 20f
) : GameObject() {
    override val renderable: Object3D?
        get() {
            val geometry = BufferGeometry()
            val vertices = mutableListOf<Float>()
            for (i in 1 until heightData.size) {
                val currentPos = Vector3(i * (width / res), 0, 0).add(offset)
                vertices.add
            }
            TODO()
        }

    private var object3D: Object3D? = null

    private val heightData: Array<Float> = Array<Float>(res) { 100f }

    override suspend fun initVisual() {
        object3D = Mesh(BoxGeometry(200, 200, 15), MeshPhongMaterial().apply {
            color = Color(0x1F8228)
        }).apply {
            position.y = -100
            enableShadows()
        }
    }
}