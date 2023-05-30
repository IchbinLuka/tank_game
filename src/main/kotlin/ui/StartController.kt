package ui

import core.Game
import ext.fromId
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import scenes.MainScene
import util.GUIHelper
import util.Localizations

class StartController: UIController() {

    override val mainGUIName: String = "start"

    override suspend fun init() {
        super.init()
        initListeners()
    }
    override fun rebuild() {
        updatePlayersDisplay()
    }

    private var playersSlider: HTMLInputElement? = null
    private var playersDisplay: Element? = null
    private var multiplayerButton: Element? = null

    private fun updatePlayersDisplay() {
        playersDisplay?.innerHTML = Localizations.getString("player_count", "count" to playersSlider!!.value)
    }

    private fun initListeners() {
        multiplayerButton = Element.fromId("multiplayerButton")
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
        Element.fromId("settingsButton")?.addEventListener("click", {
            MainScope().launch { GUIHelper.push(SettingsController()) }
        })
        multiplayerButton?.addEventListener("click", {
            MainScope().launch { GUIHelper.push(SessionBrowserController()) }
        })
        updatePlayersDisplay()
    }
}