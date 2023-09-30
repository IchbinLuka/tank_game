package ui

import ColorData
import createGame
import PlayerData
import core.Game
import ext.addClickListener
import ext.fromId
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import scenes.MainSceneSingleplayer
import util.GUIHelper
import util.Localizations
import util.Log

class LocalGameStart: UIController() {
    override val mainGUIName: String = "local_menu"

    private var playersSlider: HTMLInputElement? = null
    private var playersDisplay: Element? = null

    override fun rebuild() {
        updatePlayersDisplay()
    }

    override suspend fun init() {
        super.init()
        initListeners()
    }

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
            addClickListener {
                val playerCount = playersSlider?.value?.toIntOrNull() ?: 3
                Game.instance.currentScene = MainSceneSingleplayer(startData = createGame(List(playerCount) {
                    val (name, color) = MainSceneSingleplayer.tankColors[it]
                    PlayerData(
                        name = name,
                        color = ColorData(color.getHex().toInt()),
                    )
                }))
            }
        }
        Element.fromId("backButton")?.addClickListener {
            MainScope().launch { GUIHelper.pop() }
        } ?: Log.e("LocalGameStart", "backButton is null")
        updatePlayersDisplay()
    }
}