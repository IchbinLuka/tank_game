package scenes

import core.Game
import ext.fromId
import gameobjects.Tank
import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import three.js.Color
import three.js.Vector3
import util.GUIHelper

class StartScene: TankPresentingScene(listOf(
    Color(0x338530),
    Color(0xfc522f),
    Color(0xef3434)
).map { Tank(color = it, startPosition = Vector3()) }) {
    override suspend fun loadAssets() {
        super.loadAssets()
        GUIHelper.loadGUI("start")
        initListeners()
    }

    private var playersSlider: HTMLInputElement? = null
    private var playersDisplay: Element? = null

    private fun updatePlayersDisplay() {
        playersDisplay?.innerHTML = playersSlider!!.value
    }

    private fun initListeners() {
        playersDisplay = Element.fromId("num_players_display")
        playersSlider = (Element.fromId("num_players_slider") as HTMLInputElement).apply {
            addEventListener("input", {
                updatePlayersDisplay()
            })
            max = MainScene.tankColors.size.toString()
        }

        Element.fromId("startButton")?.apply {
            addEventListener("click", {
                Game.instance.currentScene = MainScene(numPlayers = playersSlider?.value?.toIntOrNull() ?: 3)
            })
        }
        updatePlayersDisplay()
    }
}