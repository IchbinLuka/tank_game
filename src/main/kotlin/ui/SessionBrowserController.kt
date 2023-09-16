package ui

import PlayerData
import core.ConnectionManager
import exceptions.ConnectionFailedException
import exceptions.InvalidPlayerConfigException
import ext.TAG
import ext.addClickListener
import ext.fromId
import ext.tryCast
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement
import services.APIHelper
import sessions.SessionInfo
import ui.components.SessionBrowserItem
import ui.components.SessionFetchError
import util.GUIHelper
import util.Log

class SessionBrowserController: UIController() {

    override val mainGUIName: String = "session_browser"

    private val backButton get() = Element.fromId("backButton")
    private val newButton get() = Element.fromId("newSessionButton")?.tryCast<HTMLButtonElement>()
    private val changeServerButton get() = Element.fromId("changeServerButton")
    private val loadingIndicator get() = Element.fromId("loadingIndicator")?.tryCast<HTMLElement>()

    private suspend fun joinSession(info: SessionInfo, playerData: PlayerData) {
        try {
            ConnectionManager.connect(info.id, playerData)
            val updatedSession = APIHelper.fetchSessionById(info.id)
            GUIHelper.push(SessionViewController(updatedSession, playerData))
        } catch (e: ConnectionFailedException) {
            Log.e(TAG, "Could not connect to session")
        } catch (e: InvalidPlayerConfigException) {
            Log.e(TAG, "Invalid player config")
            throw e
        }
    }


    override suspend fun init() {
        super.init()
        val container = Element.fromId("sessionItemContainer")!!
        backButton?.addClickListener {
            MainScope().launch { GUIHelper.pop() }
        }

        changeServerButton?.addClickListener {
            MainScope().launch { GUIHelper.pushReplacement(ServerSettingsController()) }
        }

        try {
            val sessions = APIHelper.fetchSessions()

            for (session in sessions) {
                insertComponent(
                    SessionBrowserItem(session) {
                        MainScope().launch {
                            GUIHelper.push(PlayerConfigurationController { player ->
                                joinSession(it, player)
                            })
                        }
                    },
                    container
                )
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Could not fetch sessions: ${e.message}")
            insertComponent(
                SessionFetchError(),
                container
            )
        } finally {
            loadingIndicator?.style?.display = "none"
        }
        newButton?.disabled = false
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