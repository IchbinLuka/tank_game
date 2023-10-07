package ui.views

import PlayerData
import ext.s
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import kotlinx.html.span
import kotlinx.html.style
import sessions.SessionInfo
import util.hexStringFromInt

fun UI.sessionBrowserItem(
    session: SessionInfo,
    onJoin: (SessionInfo) -> Unit
) {
    div {
        style = "width: 100%; display: flex; justify-content: space-between"
        span { +session.name }
        span {
            span { s("player_count", "count" to session.players.size) }
            menuButton {
                s("join")
                onClickFunction = { onJoin(session) }
            }
        }
    }
}

fun UI.sessionLoadError() {
    div {
        s("error_could_not_fetch_sessions")
        style = "text-align: center"
    }
}

fun UI.sessionItem(player: PlayerData) {
    div(classes = "listItem") {
        style = "width: 200px"
        span { +player.name }
        span { style = "background: ${hexStringFromInt(player.color.value)}; width: 30px" }
    }
}