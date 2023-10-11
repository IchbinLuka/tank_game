package ui.views

import PlayerData
import ext.s
import kotlinx.html.div
import kotlinx.html.js.onClickFunction
import kotlinx.html.span
import kotlinx.html.style
import sessions.SessionInfo
import util.hexStringFromInt

fun <T> UI<T>.sessionBrowserItem(
    session: SessionInfo,
    onJoin: (SessionInfo) -> Unit
): T = div {
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

fun <T> UI<T>.sessionLoadError(): T = div {
    s("error_could_not_fetch_sessions")
    style = "text-align: center"
}

fun <T> UI<T>.sessionItem(player: PlayerData): T = div(classes = "listItem") {
    style = "width: 200px"
    span { +player.name }
    span { style = "background: ${hexStringFromInt(player.color.value)}; width: 30px" }
}