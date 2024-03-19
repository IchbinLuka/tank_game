package ui

import core.Game
import ext.s
import kotlinx.html.js.onClickFunction
import scenes.DiscordStartScene
import scenes.GameEndScene
import services.APIHelper
import ui.views.backToMenuButton
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
                if (APIHelper.discord.shouldUseDiscord()) {
                    menuButton {
                        s("back_to_lobby")
                        onClickFunction = {
                            Game.instance.currentScene = DiscordStartScene()
                        }
                    }
                } else {
                    backToMenuButton()
                }
            }
        )
    }
}