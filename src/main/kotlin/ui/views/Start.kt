package ui.views

import ext.s
import kotlinx.html.div
import kotlinx.html.js.onClickFunction

fun UI.startScreen(
    onLocalGame: () -> Unit,
    onMultiplayerGame: () -> Unit,
    onGuide: () -> Unit,
    onSettings: () -> Unit
) {
    menuPage(
        title = { +"Tanks 3D" },
        listOf {
            div {
                menuButton {
                    onClickFunction = { onLocalGame() }
                    s("local_game")
                }
            }
            div {
                menuButton {
                    onClickFunction = { onMultiplayerGame() }
                    s("online_game")
                }
            }
            div {
                menuButton {
                    onClickFunction = { onGuide() }
                    s("how_to_play")
                }
            }
            div {
                menuButton {
                    onClickFunction = { onSettings() }
                    s("settings")
                }
            }
        }
    )
}

