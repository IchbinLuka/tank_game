import core.Game
import scenes.MainScene

fun main() {
    // val game = Game(HeightmapVisualizer(heightMap = GroundNoise(300, 20).allValues(), heightScale = 3f))
    val game = Game(MainScene())
    game.mainLoop()
    // val renderer = HeightmapRenderer()
    // renderer.render(ValueNoise(width = 200, height = 200).calculate())
}