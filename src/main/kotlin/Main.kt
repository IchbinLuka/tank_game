import core.Game
import scenes.HeightmapVisualizer
import types.rust.init
import types.rust.init_panic_hook
import types.rust.value_noise

fun main() {
    // val game = Game(HeightmapVisualizer(heightMap = GroundNoise(300, 20).allValues(), heightScale = 3f))
    // val game = Game(MainScene())
    // game.mainLoop()
    init().then {
        init_panic_hook()
        val output = value_noise(50, 50, 8)
        val game = Game(HeightmapVisualizer(heightMap = output.asDynamic(), heightScale = 3f))
        game.mainLoop()
        println(output)
    }
    // val renderer = HeightmapRenderer()
    // renderer.render(ValueNoise(width = 200, height = 200).calculate())
}