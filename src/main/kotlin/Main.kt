import core.Game
import scenes.HeightmapVisualizer
import scenes.test.GroundTestScene
import scenes.test.MeshTestScene
import scenes.test.WaitingScene
import util.noise.GroundNoise

fun main() {
    // val game = Game(HeightmapVisualizer(heightMap = GroundNoise(300, 20).allValues(), heightScale = 3f))
    val game = Game(GroundTestScene())
    game.mainLoop()
    // val renderer = HeightmapRenderer()
    // renderer.render(ValueNoise(width = 200, height = 200).calculate())
}