package ui

import ext.fromId
import org.w3c.dom.Element
import util.GUIHelper

class GameOver(
    private val name: String
): UIController() {

    override suspend fun initAssets() {
        GUIHelper.loadGUI("game_over")
        Element.fromId("winner_name")?.apply {
            innerHTML = name
        }
    }
}