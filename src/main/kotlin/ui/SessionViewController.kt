package ui

import GameMessage
import GameMessageData
import PlayerData
import core.ConnectionManager
import core.Game
import core.multiplayer.MultiplayerSynchronized
import ext.addClickListener
import ext.fromId
import ext.tryCast
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import org.w3c.dom.HTMLButtonElement
import scenes.MainSceneMultiplayer
import sessions.SessionInfo
import sessions.Topic
import ui.components.SessionInfoItem
import util.GUIHelper

class SessionViewController(
    private var session: SessionInfo,
    private val self: PlayerData
) : UIController(), MultiplayerSynchronized {

    override val name: String = "session_view_controller"

    override val mainGUIName: String = "session_view"

    private val startButton get() = Element.fromId("startButton")?.tryCast<HTMLButtonElement>()


    override suspend fun init() {
        ConnectionManager.register(this)
        super.init()
        updatePlayers(session)
    }

    private suspend fun updatePlayers(session: SessionInfo) {
        val container = Element.fromId("playerInfoContainer") ?: return
        container.innerHTML = ""
        for (player in session.players) {
            insertComponent(SessionInfoItem(player), container)
        }
        startButton?.disabled = session.players.size < 2
    }

    override fun rebuild() {
        if (session.host == self) {
            startButton?.let {
                it.style.visibility = "visible"
                it.addClickListener {
                    ConnectionManager.broadcast(
                        GameMessage.fromGameMessageData(
                            GameMessageData.GameStartRequest,
                            Topic.GLOBAL
                        )
                    )
                }
            }
        }
    }

    override val topic: String = Topic.SESSION_STATE

    override fun onMessage(message: GameMessageData) {
        when (message) {
            is GameMessageData.SessionStateUpdate -> {
                session = message.session
                MainScope().launch { updatePlayers(session) }
            }
            is GameMessageData.GameStartData -> {
                Game.instance.currentScene = MainSceneMultiplayer(message, self)
            }
            is GameMessageData.Disconnect -> {
                MainScope().launch { GUIHelper.pop() }
            }
            else -> {}
        }
    }


}