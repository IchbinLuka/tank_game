package ui

import core.Game
import ext.fromId
import org.w3c.dom.Element
import scenes.GameEndScene
import scenes.StartScene
import util.Localizations

class GameEndController(
    private val scene: GameEndScene
): UIController() {

    override val mainGUIName: String = "game_over"
    override suspend fun init() {
        super.init()
        initListener()
    }
    override fun rebuild() {
        Element.fromId("winner")?.innerHTML = Localizations.getString(
            "player_wins",
            "name" to scene.winners.first().name
        )
    }

    private fun initListener() {
        Element.fromId("returnButton")?.addEventListener("click", {
            Game.instance.currentScene = StartScene()
        })
    }
}