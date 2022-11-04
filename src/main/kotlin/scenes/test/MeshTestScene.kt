package scenes.test

import core.gameobjects.GameObject
import ext.aspectRatio
import kotlinx.browser.window
import org.khronos.webgl.Float32Array
import scenes.GameScene
import three.js.*
import kotlin.math.PI

class MeshTestScene : GameScene() {

    override val objects: HashSet<GameObject>
        get() = hashSetOf()

    override val scene = Scene().apply {
        add(HemisphereLight(skyColor = 0x47C4F3, groundColor = 0xABAF9E, intensity = 1))
        add(AmbientLight(color = 0xffffff, intensity = 1))
        background = Color(0x5f9be5)
        val geometry = BufferGeometry()
        val positions = arrayOf(
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,

            1.0f,  1.0f,  1.0f,
            -1.0f,  1.0f,  1.0f,
            -1.0f, -1.0f,  1.0f,

            -1.0f, -1.0f, 1.0f,
            -2.0f, -1.0f, 1.0f,
            -1.0f, -2.0f, 1.0f
        )
        val positions2 = arrayOf(
            -1.0f, -1.0f,  1.0f,
            1.0f, -1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            -1.0f,  1.0f,  1.0f,
            -2.0f, -1.0f, 1.0f,
            -1.0f, -2.0f, 1.0f
        )

        val indices = mutableListOf<Int>(
            0, 1, 2,
            2, 3, 0,
            0, 4, 5
        )

        val normals = Array<Float>(27) {
            if ((it + 1) % 3 == 0) 1f else 0f
        }
        // add(Mesh(BoxGeometry(0.5, 0.5, 0.5), MeshPhongMaterial().apply { color = Color(0xff0000) }))
        geometry.setAttribute("position", BufferAttribute(Float32Array(positions2).asDynamic(), 3))
        geometry.setIndex(indices.toTypedArray())
        geometry.setAttribute("normal", BufferAttribute(Float32Array(normals).asDynamic(), 3))
        val mesh = Mesh(geometry, MeshPhongMaterial().apply {
            color  = Color(0x00ff00)
        })
        mesh.rotation.z = 2 * PI
        add(mesh)
    }
    override val camera: Camera = PerspectiveCamera(75, window.aspectRatio, 0.1, 1000).apply {
        position.z = 3.0
        position.y = 0.0
        position.x = 1.0
        rotation.x = 0.1
        rotation.y = 0.5

    }

    override fun update(delta: Double) {}
}