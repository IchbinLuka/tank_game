package ui

import ColorData
import PlayerData
import core.Game
import createGame
import ext.fromId
import ext.s
import ext.tryCast
import kotlinx.html.*
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onInputFunction
import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import scenes.MainSceneSingleplayer
import ui.views.backButton
import ui.views.menuButton
import ui.views.menuElement
import ui.views.menuPage
import util.Localizations

class LocalGameStart: UIController() {
    override val mainUIBuilder: UIBuilder = {
        menuPage(
            title = { s("local_game") },
            sections = listOf {
                menuElement {
                    label {
                        htmlFor = "num_players_slider"
                        style = "margin-right: 10px"
                        span { id = "num_players_display" }
                    }
                    input(type = InputType.range, classes = "slider") {
                        id = "num_players_slider"
                        min = "2"
                        max = MainSceneSingleplayer.tankColors.size.toString()
                        value = "3"

                        onInputFunction = { updatePlayersDisplay() }
                    }
                }
                div {
                    menuButton {
                        onClickFunction = { onStartClick() }
                        s("start_game")
                    }
                }
                div {
                    style = "margin-top: 5px"
                    backButton()
                }
            }
        )
    }

    private val playersSlider: HTMLInputElement? get() = Element.fromId("num_players_slider")?.tryCast()
    private val playersDisplay: Element? get() = Element.fromId("num_players_display")

    override fun rebuild() {
        updatePlayersDisplay()
    }

    override suspend fun init() {
        super.init()
        updatePlayersDisplay()
    }

    private fun onStartClick() {
        val playerCount = playersSlider?.value?.toIntOrNull() ?: 3
        Game.instance.currentScene = MainSceneSingleplayer(startData = createGame(List(playerCount) {
            val (name, color) = MainSceneSingleplayer.tankColors[it]
            PlayerData(
                name = name,
                color = ColorData(color.getHex().toInt()),
            )
        }))
    }

    private fun updatePlayersDisplay() {
        playersDisplay?.innerHTML = Localizations.getString("player_count", "count" to playersSlider!!.value)
    }
}