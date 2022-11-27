import types.rust.greet
import types.rust.init
import types.rust.value_noise

fun main() {
    // val game = Game(HeightmapVisualizer(heightMap = GroundNoise(300, 20).allValues(), heightScale = 3f))
    // val game = Game(MainScene())
    // game.mainLoop()
    init().then {
        greet()
        println(value_noise(1, 2))
    }
    // val renderer = HeightmapRenderer()
    // renderer.render(ValueNoise(width = 200, height = 200).calculate())
}