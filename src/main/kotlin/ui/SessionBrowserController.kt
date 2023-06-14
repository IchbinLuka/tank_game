package ui

import PlayerData
import core.ConnectionManager
import exceptions.ConnectionFailedException
import ext.TAG
import ext.addClickListener
import ext.fromId
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import services.APIHelper
import sessions.SessionInfo
import ui.components.SessionBrowserItem
import util.GUIHelper
import util.Log

class SessionBrowserController: UIController() {

    override val mainGUIName: String = "session_browser"

    private val backButton get() = Element.fromId("backButton")
    private val newButton get() = Element.fromId("newSessionButton")

    private fun joinSession(info: SessionInfo, playerData: PlayerData) {
        MainScope().launch {
            try {
                ConnectionManager.connect(info.id, playerData)
                val updatedSession = APIHelper.fetchSessionById(info.id)
                GUIHelper.push(SessionViewController(updatedSession))
            } catch (e: ConnectionFailedException) {
                Log.e(TAG, "Could not connect to session")
            }
        }
    }


    override suspend fun init() {
        super.init()
        val sessions = APIHelper.fetchSessions()
        val container = Element.fromId("sessionItemContainer")!!
        for (session in sessions) {
            insertComponent(
                SessionBrowserItem(session) { session ->
                    MainScope().launch {
                        GUIHelper.push(PlayerConfigurationController { player ->
                            joinSession(session, player)
                        })
                    }
                },
                container
            )
        }
        backButton?.addClickListener {
            MainScope().launch { GUIHelper.pop() }
        }
        newButton?.addClickListener {
            MainScope().launch {
                GUIHelper.push(PlayerConfigurationController { player ->
                    MainScope().launch {
                        GUIHelper.pushReplacement(SessionConfigurationController(player) {
                            MainScope().launch {
                                val info = APIHelper.createSession(it)
                                joinSession(info, player)
                            }
                        })
                    }
                })
            }
        }
    }

    override fun rebuild() {}
}