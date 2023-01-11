package ui

import ext.fromId
import org.w3c.dom.Element
import scenes.GameState
import scenes.MainScene
import util.GUIHelper

class MainTopBar(
    private val scene: MainScene
) {
    private val healthElement by lazy { Element.fromId("health") }

    init {
        scene.addListener { _, _, new ->
            updateUI(new)
        }
    }

    suspend fun init() {
        GUIHelper.loadTopBar("tool_bar")
    }

    private fun updateUI(state: GameState = scene.currentState) {
        healthElement?.innerHTML = state.currentTank.health.toString()
    }


}