package ui

import ColorData
import PlayerData
import exceptions.InvalidPlayerConfigException
import ext.TAG
import ext.fromId
import ext.s
import ext.tryCast
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.html.*
import kotlinx.html.js.onClickFunction
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import ui.views.backButton
import ui.views.menuButton
import ui.views.menuElement
import ui.views.menuPage
import util.Log

class PlayerConfigurationController(
    private val onSubmitted: suspend (PlayerData) -> Unit,
) : UIController() {

    override val mainUIBuilder: UIBuilder = {
        menuPage(
            title = { s("create_player") },
            sections = listOf {
                menuElement {
                    label {
                        span { s("player_name") }
                        input(type = InputType.text) { id = "player_name" }
                    }
                }
                menuElement {
                    label {
                        span { s("player_color") }
                        input(type = InputType.color) { id = "player_color"; value = "#00ff00" }
                    }
                }
                div {
                    menuElement {
                        id = "invalid_config_view"
                        s("please_use_other_name_color")
                        style = "visibility: hidden; font-size: 20px; padding: 5px"
                    }
                }

                div(classes = "gridContainer") {
                    backButton()
                    menuButton {
                        s("next")
                        onClickFunction = { onSubmit() }
                    }
                }
            }
        )
    }

    private val nameTextField get() = Element.fromId("player_name") as HTMLInputElement
    private val colorInput get() = Element.fromId("player_color") as HTMLInputElement

    private val invalidConfigView: HTMLElement? get() = Element.fromId("invalid_config_view")?.tryCast()

    private fun onSubmit() {
        if (nameTextField.value.trim() != "") {
            MainScope().launch {
                try {
                    onSubmitted(
                        PlayerData(
                            nameTextField.value,
                            ColorData(colorInput.value.substring(1).toInt(16))
                        )
                    )
                } catch (e: InvalidPlayerConfigException) {
                    Log.e(TAG, "Invalid player config")
                    invalidConfigView?.style?.visibility = "visible"
                }
            }
        }
    }
}