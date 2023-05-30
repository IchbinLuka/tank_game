package ui.components

import sessions.Session

class SessionBrowserItem(
    private val info: Session
): UIComponent() {
    override val componentSource: String = "session_browser_item"

    override val replacements: HashMap<String, String> = hashMapOf(
        "name" to info.name,
        "num_players" to info.players.size.toString()
    )
}