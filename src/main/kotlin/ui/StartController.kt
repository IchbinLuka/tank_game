package ui

import ColorData
import PlayerData
import core.Game
import createGame
import ext.fromId
import ext.tryCast
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement
import scenes.MainSceneSingleplayer
import services.APIHelper
import util.GUIHelper
import util.Localizations

class StartController: UIController() {

    override val mainGUIName: String = "start"

    override suspend fun init() {
        super.init()
        initListeners()
        MainScope().launch {
            if (!APIHelper.serverAvailable()) {
                multiplayerButton?.disabled = true
            }
        }
    }
    override fun rebuild() {
        updatePlayersDisplay()
    }

    private var playersSlider: HTMLInputElement? = null
    private var playersDisplay: Element? = null
    private val multiplayerButton: HTMLButtonElement? get() = Element.fromId("multiplayerButton")?.tryCast()

    private fun updatePlayersDisplay() {
        playersDisplay?.innerHTML = Localizations.getString("player_count", "count" to playersSlider!!.value)
    }

    private fun initListeners() {
        playersDisplay = Element.fromId("num_players_display")
        playersSlider = (Element.fromId("num_players_slider") as HTMLInputElement).apply {
            addEventListener("input", {
                updatePlayersDisplay()
            })
            max = MainSceneSingleplayer.tankColors.size.toString()
        }

        Element.fromId("startButton")?.apply {
            addEventListener("click", {
                val playerCount = playersSlider?.value?.toIntOrNull() ?: 3
                Game.instance.currentScene = MainSceneSingleplayer(startData = createGame(List(playerCount) {
                    val (name, color) = MainSceneSingleplayer.tankColors[it]
                    PlayerData(
                        name = name,
                        color = ColorData(color.getHex().toInt()),
                    )
                }))
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