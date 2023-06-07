package ui.components

import ext.fromId
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import sessions.SessionInfo
import ui.PlayerConfigurationController
import util.GUIHelper
import util.Log

class SessionBrowserItem(
    private val info: SessionInfo
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
            MainScope().launch {
                GUIHelper.push(PlayerConfigurationController {
                    Log.d("SessionBrowserItem", it.toString())
                })
            }
        })
    }
}