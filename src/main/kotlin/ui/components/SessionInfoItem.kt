package ui.components

import PlayerData

class SessionInfoItem(
    val player: PlayerData
): UIComponent() {
    override val componentSource: String = "session_info_item"
    override val replacements: HashMap<String, String> = hashMapOf(
        "name" to player.name,
        "color" to player.color.toString()
    )
}