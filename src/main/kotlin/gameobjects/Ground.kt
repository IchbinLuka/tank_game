package gameobjects

import core.GameObject
import extensions.enableShadows
import extensions.toFloatList
import org.khronos.webgl.Float32Array
import three.js.*

class Ground(
    private val res: Int = 200,
    private val width: Float = 200f,
    private val offset: Vector3 = Vector3(0, 0, 0),
    private val height: Float = 100f,
    private val depth: Float = 20f
) : GameObject() {
    override val renderable: Object3D
        get() {
            val geometry = BufferGeometry()
            val vertices = mutableListOf<Float>()
            for (i in 1 until heightData.size) {
                val height = heightData[i]
                val currentPos = Vector3(i * (width / res), 0, 0).add(offset)
                val currentTop = currentPos.add(Vector3(0, height, 0))
                val currentBack = currentTop.add(Vector3(0, 0, depth))

                val previous = currentPos.add(Vector3(-width, 0, 0))
                val previousTop = previous.add(Vector3(0, height, 0))
                val previousBack = previousTop.add(Vector3(0, 0, depth))
                vertices.addAll(previous.toFloatList())
                vertices.addAll(currentPos.toFloatList())
                vertices.addAll(previousTop.toFloatList())

                vertices.addAll(currentPos.toFloatList())
                vertices.addAll(previousTop.toFloatList())
                vertices.addAll(currentTop.toFloatList())

                vertices.addAll(previousTop.toFloatList())
                vertices.addAll(currentTop.toFloatList())
                vertices.addAll(previousBack.toFloatList())

                vertices.addAll(currentTop.toFloatList())
                vertices.addAll(previousBack.toFloatList())
                vertices.addAll(currentBack.toFloatList())

            }
            geometry.setAttribute("position", BufferAttribute(Float32Array(vertices.toTypedArray()).asDynamic(), 3))
            return Mesh(geometry, MeshPhongMaterial().apply { color = Color(0x00ff00) })
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