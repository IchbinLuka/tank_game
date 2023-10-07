package ui

import PlayerData
import core.ConnectionManager
import exceptions.ConnectionFailedException
import exceptions.InvalidPlayerConfigException
import ext.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.html.id
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import kotlinx.html.style
import org.w3c.dom.Element
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement
import services.APIHelper
import sessions.SessionInfo
import ui.views.*
import util.GUIHelper
import util.Log

class SessionBrowserController: UIController() {

    override val mainUIBuilder: UIBuilder = {
        menuPage(
            title = { s("online_game") },
            sections = listOf {
                menuElement {
                    id = "sessionItemContainer"
                    style = "overflow-y: scroll; height: 300px; width: 500px"

                    div {
                        id = "loadingIndicator"
                        style = "margin-top: 50px"
                        loadingIndicator()
                    }
                }

                div {
                    div(classes = "gridContainer") {
                        style = "grid-template-columns: auto auto auto"
                        backButton()
                        menuButton {
                            s("change_server")
                            onClickFunction = { onChangeServer() }
                        }
                        menuButton {
                            s("new_session")
                            onClickFunction = { onNewSession() }
                        }
                    }
                }
            }
        )
    }
    private val newButton get() = Element.fromId("newSessionButton")?.tryCast<HTMLButtonElement>()
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

    private fun onNewSession() {
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

    private fun onChangeServer() {
        MainScope().launch {
            GUIHelper.pushReplacement(ServerSettingsController())
        }
    }

    override suspend fun init() {
        super.init()
        val container = Element.fromId("sessionItemContainer")!!

        try {
            val sessions = APIHelper.fetchSessions()

            container.replace {
                for (session in sessions) {
                    sessionBrowserItem(session) {
                        MainScope().launch {
                            GUIHelper.push(PlayerConfigurationController { player ->
                                joinSession(it, player)
                            })
                        }
                    }
                }
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Could not fetch sessions: ${e.message}")
            container.replace {
                sessionLoadError()
            }
        } finally {
            loadingIndicator?.style?.display = "none"
        }
        newButton?.disabled = false
    }
}