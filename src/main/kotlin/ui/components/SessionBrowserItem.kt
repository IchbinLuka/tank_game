package ui.components

import ext.fromId
import org.w3c.dom.Element
import sessions.SessionInfo

class SessionBrowserItem(
    private val info: SessionInfo,
    private val onJoin: (SessionInfo) -> Unit
): UIComponent() {
    override val componentSource: String = "session_browser_item"

    override val replacements: HashMap<String, String> = hashMapOf(
        "name" to info.name,
        "num_players" to info.players.size.toString(),
        "session_id" to info.id.toString()
    )

    override fun onMounted() {
        super.onMounted()
        Element.fromId("button_${info.id}")?.addEventListener("click", {
            onJoin(info)
        })
    }
}

class SessionFetchError : UIComponent() {
    override val componentSource: String = "session_fetch_error"
}