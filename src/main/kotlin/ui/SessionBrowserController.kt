package ui

import ext.fromId
import kotlinx.browser.window
import org.w3c.dom.Element
import org.w3c.fetch.RequestInit
import services.APIHelper
import ui.components.SessionBrowserItem

class SessionBrowserController: UIController() {

    override val mainGUIName: String = "session_browser"


    override suspend fun init() {
        super.init()
        val sessions = APIHelper.fetchSessions()
        val container = Element.fromId("sessionItemContainer")!!
        for (session in sessions) {
            insertComponent(SessionBrowserItem(session), container)
        }
    }

    override fun rebuild() {}
}