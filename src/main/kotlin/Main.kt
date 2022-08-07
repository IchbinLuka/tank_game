import core.Game
import scenes.MainScene
import scenes.ParticleTestScene

fun main() {
    val game = Game(MainScene())
    game.mainLoop()
}