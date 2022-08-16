import core.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.WindowOrWorkerGlobalScope
import scenes.HeightmapVisualizer
import scenes.MainScene
import scenes.ParticleTestScene
import tests.HeightmapRenderer
import util.noise.ValueNoise

fun main() {
    val game = Game(HeightmapVisualizer(heightMap = ValueNoise(200, 200).calculate(), heightScale = 3f))
    // val game = Game(MainScene())
    game.mainLoop()
    // val renderer = HeightmapRenderer()
    // renderer.render(ValueNoise(width = 200, height = 200).calculate())

}