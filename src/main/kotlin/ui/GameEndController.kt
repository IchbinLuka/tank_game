package ui

import core.Game
import ext.s
import kotlinx.html.js.onClickFunction
import scenes.GameEndScene
import scenes.StartScene
import ui.views.menuButton
import ui.views.menuPage

class GameEndController(
    private val scene: GameEndScene
): UIController() {

    override val mainUIBuilder: UIBuilder = {
        menuPage(
            title = {
                s("player_wins", "name" to scene.winners.first().name)
            },
            sections = listOf {
                menuButton {
                    s("return_to_menu")
                    onClickFunction = {
                        Game.instance.currentScene = StartScene()
                    }
                }
            }
        )
    }
}