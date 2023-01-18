import core.Game
import scenes.StartScene

fun main() {
    val game = Game(StartScene())
    game.mainLoop()
}