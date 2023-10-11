package ui.views

import core.Player
import ext.toVector3
import kotlinx.html.div
import kotlinx.html.style
import three.js.Color

fun <T> UI<T>.nameTag(player: Player): T = div {
    val whiteFont = Color(0xffffff).toVector3().distanceTo(player.color.toVector3()).toDouble() > 0.5
    style = """
        font-size: 10px;
        background: #${player.color.getHexString()};
        color: ${if (whiteFont) "white" else "black"};
        padding: 2px;
        border-radius: 2px;
    """.trimIndent()
    +player.name
}