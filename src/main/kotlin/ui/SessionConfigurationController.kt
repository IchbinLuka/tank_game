package ui

import PlayerData
import ext.fromId
import ext.s
import ext.tryCast
import kotlinx.html.*
import kotlinx.html.js.onClickFunction
import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import sessions.SessionCreationInfo
import ui.views.backButton
import ui.views.menuButton
import ui.views.menuPage

class SessionConfigurationController(
    val player: PlayerData,
    val onSubmit: (SessionCreationInfo) -> Unit
): UIController() {

    override val mainUIBuilder: UIBuilder = {
        menuPage(
            title = { s("new_session") },
            sections = listOf {
                div {
                    style = "display: flex; align-items: center"
                    label("menu-element") {
                        span {
                            s("player_name")
                        }
                        input(type = InputType.text) {
                            id = "sessionName"
                            name = "session_name"
                            value = ""
                        }
                    }
                }
                div("gridContainer") {
                    backButton()
                    menuButton {
                        s("next")
                        onClickFunction = { onSubmit() }
                    }
                }
            }
        )
    }

    private val textField get() = Element.fromId("sessionName")?.tryCast<HTMLInputElement>()

    private fun onSubmit() {
        textField?.value?.let {
            if (it.trim() != "") {
                onSubmit(SessionCreationInfo(it, player))
            }
        }
    }
}