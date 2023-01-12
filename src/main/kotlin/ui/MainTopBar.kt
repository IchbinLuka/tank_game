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
    private val nameElement by lazy { Element.fromId("name") }

    init {
        scene.addListener { _, _, new ->
            updateUI(new)
        }
    }

    suspend fun init() {
        GUIHelper.loadTopBar("tool_bar")
        updateUI()
    }

    private fun updateUI(state: GameState = scene.currentState) {
        healthElement?.innerHTML = state.currentTank.health.toString()
        nameElement?.innerHTML = state.currentTank.name
    }


}