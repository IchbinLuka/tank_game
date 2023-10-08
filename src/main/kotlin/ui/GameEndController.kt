package ui

import ext.s
import scenes.GameEndScene
import ui.views.backToMenuButton
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
                backToMenuButton()
            }
        )
    }
}