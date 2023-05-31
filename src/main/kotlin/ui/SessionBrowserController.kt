package ui

import ext.fromId
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import services.APIHelper
import ui.components.SessionBrowserItem
import util.GUIHelper

class SessionBrowserController: UIController() {

    override val mainGUIName: String = "session_browser"

    private val backButton get() = Element.fromId("backButton")
    private val newButton get() = Element.fromId("newSessionButton")


    override suspend fun init() {
        super.init()
        val sessions = APIHelper.fetchSessions()
        val container = Element.fromId("sessionItemContainer")!!
        for (session in sessions) {
            insertComponent(SessionBrowserItem(session), container)
        }
        backButton?.addEventListener("click", {
            MainScope().launch { GUIHelper.pop() }
        })
        newButton?.addEventListener("click", {

        })
    }

    override fun rebuild() {}
}