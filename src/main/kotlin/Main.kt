import core.Game
import ext.TAG
import kotlinx.browser.document
import kotlinx.browser.window
import scenes.HeightmapVisualizer
import scenes.MainScene
import types.rust.init
import types.rust.init_panic_hook
import types.rust.value_noise
import util.GUIHelper
import util.Log
import util.noise.ValueNoise
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
suspend fun main() {
    // val game = Game(HeightmapVisualizer(heightMap = GroundNoise(300, 20).allValues(), heightScale = 3f))
    val game = Game(MainScene())
    game.mainLoop()
    /*init().then {
        init_panic_hook()

        val width = 1000
        val height = 1000
        val octaves = 80

        val rustTime = measureTime {
            value_noise(width, height, octaves)
        }
        val kotlinTime = measureTime {
            ValueNoise(width, height, octaves, 5, 5, 20).allValues()
        }
        println("rust: $rustTime, kotlin: $kotlinTime")
    }*/
    // val renderer = HeightmapRenderer()
    // renderer.render(ValueNoise(width = 200, height = 200).calculate())
}