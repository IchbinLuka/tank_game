package ui

import core.Game
import ext.fromId
import org.w3c.dom.Element
import scenes.GameEndScene
import scenes.StartScene

class GameEndController(
    private val scene: GameEndScene
): UIController() {

    override suspend fun init() {
        super.init()
        initListener()
    }
    override fun rebuild() {
        Element.fromId("winnerName")?.innerHTML = scene.winners.first().name
    }

    private fun initListener() {
        Element.fromId("returnButton")?.addEventListener("click", {
            Game.instance.currentScene = StartScene()
        })
    }
}