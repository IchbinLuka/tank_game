package ui.views

import ext.s
import kotlinx.html.*
import kotlinx.html.js.onClickFunction

fun DIV.startScreen(
    onLocalGame: () -> Unit,
    onMultiplayerGame: () -> Unit,
    onGuide: () -> Unit,
    onSettings: () -> Unit
) {
    menuPage(
        title = "Tanks 3D",
        listOf {
            div {
                button(classes = "menu-element button") {
                    onClickFunction = { onLocalGame() }
                    s("local_game")
                }
            }
            div {
                button(classes = "menu-element button") {
                    onClickFunction = { onMultiplayerGame() }
                    s("online_game")
                }
            }
            div {
                button(classes = "menu-element button") {
                    onClickFunction = { onGuide() }
                    s("how_to_play")
                }
            }
            div {
                button(classes = "menu-element button") {
                    onClickFunction = { onSettings() }
                    s("settings")
                }
            }
        }
    )
}

fun DIV.menuPage(title: String, sections: List<DIV.() -> Unit>) {
    style = "width: 100%; height: 100%"
    div("center menuPage") {
        div("menuContainer") {
            h1("menu-element menuTitle") { +title }
        }
        for (section in sections) {
            div("menuContainer") {
                section()
            }
        }
    }
}

