import core.Game
import ext.aspectRatio
import ext.minus
import stats.js.Stats
import three.js.*
import kotlinx.browser.document
import kotlinx.browser.window

class Cube {
    private val clock = Clock()
    private val camera = PerspectiveCamera(75, window.aspectRatio, 0.1, 1000).apply {
        position.z = 5
    }

    private val renderer = WebGLRenderer().apply {
        document.body?.appendChild(domElement)
        setSize(window.innerWidth, window.innerHeight)
        setPixelRatio(window.devicePixelRatio)
    }

    private val stats = Stats().apply {
        showPanel(0) // 0: fps, 1: ms, 2: mb, 3+: custom
        document.body?.appendChild(domElement)
        with (domElement.style) {
            position="fixed"
            top="0px"
            left="0px"
        }
    }

    private val cube = Mesh(BoxGeometry(1, 1, 1), MeshPhongMaterial().apply { color = Color(0x0000ff) })

    private val test = Mesh(CylinderGeometry(radiusTop = 10, radiusBottom = 30, height = 50), MeshBasicMaterial().apply { color = Color(0xffffff) })

    private val scene = Scene().apply {
        add(cube)
        add(test)
        test.position.add(Vector3(15, -2, 1))
        add(DirectionalLight(0xffffff, 1).apply { position.set(-1, 2, 4) })
        add(AmbientLight(0x404040, 1))
    }

    init {
        window.onresize = {
            camera.aspect = window.aspectRatio
            camera.updateProjectionMatrix()

            renderer.setSize(window.innerWidth, window.innerHeight)
        }
    }

    fun animate() {
        stats.begin()
        val delta = clock.getDelta().toDouble()

        window.onkeydown = {
            var vector3: Vector3 = Vector3()
            if (it.keyCode == 39) {
                vector3 = Vector3(1, 0, 0)
            } else if (it.keyCode == 40) {
                vector3 = Vector3(0, -1, 0)
            }
            scene.position.add(vector3.multiplyScalar(delta))
        }

        cube.rotation.x -= delta
        cube.rotation.y -= delta

        renderer.render(scene, camera)

        stats.end()

        window.requestAnimationFrame { animate() }
    }
}

fun main() {
    Game.mainLoop()
}