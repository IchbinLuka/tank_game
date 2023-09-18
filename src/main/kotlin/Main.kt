import core.Game
import scenes.StartScene
import util.GUIHelper

fun main() {
    val game = Game(StartScene())
    GUIHelper.initListeners() // TODO: this call should be unnecessary
    game.mainLoop()
//    MainScope().launch {
//        GUIHelper.push(JoyStickTestController())
//    }
}