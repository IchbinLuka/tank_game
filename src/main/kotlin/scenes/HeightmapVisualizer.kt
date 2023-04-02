package scenes

import core.HeightMap
import ext.aspectRatio
import kotlinx.browser.window
import org.khronos.webgl.Float32Array
import three.js.*

class HeightmapVisualizer(
    private val pointDistance: Float = 1f,
    private val heightScale: Float = 1f,
    private val heightMap: HeightMap
): GameScene() {

    override val camera = PerspectiveCamera(75, window.aspectRatio, 0.1, 1000).apply {
        position.z = 4.0
    }

    private val geometry = BufferGeometry()

    override suspend fun preloadAssets() {
        super.preloadAssets()
        scene.apply {
            background = Color(0x4ff4f7)
            generateGeometry(heightMap)
            add(AmbientLight(color = Color(0xffffff)))
            add(Points(geometry, PointsMaterial().apply {
                color = Color(0xffffff)
                size = 1
            }))
        }
    }

    private fun generateGeometry(data: HeightMap) {
        val positions = mutableListOf<Float>()
        data.forEachIndexed { i, doubles ->
            doubles.forEachIndexed { j, d ->
                positions.addAll(arrayOf(
                    i * pointDistance,
                    d.toFloat() * heightScale,
                    j * pointDistance
                ))
            }
        }
        geometry.setAttribute("position", BufferAttribute(Float32Array(positions.toTypedArray()).asDynamic(), 3))

    }

    override fun update(delta: Double) {}

}