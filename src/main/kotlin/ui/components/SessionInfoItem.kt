package ui.components

import PlayerData
import util.hexStringFromInt

class SessionInfoItem(
    val player: PlayerData
): UIComponent() {
    override val componentSource: String = "session_info_item"
    override val replacements: HashMap<String, String> = hashMapOf(
        "name" to player.name,
        "color" to hexStringFromInt(player.color.value)
    )
}