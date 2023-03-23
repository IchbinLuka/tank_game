package ui

import ext.fromId
import org.w3c.dom.Element

class GameOver(
    private val name: String
): UIController() {

    override val mainGUIName: String = "game_over"

    override fun rebuild() {
        Element.fromId("winner_name")?.apply {
            innerHTML = name
        }
    }
}