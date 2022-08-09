import core.Game
import scenes.ParticleTestScene

fun main() {
    val game = Game(ParticleTestScene())
    game.mainLoop()
}