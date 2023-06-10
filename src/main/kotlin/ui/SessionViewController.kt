package ui

import GameMessageData
import MessageType
import core.ConnectionManager
import core.Game
import core.multiplayer.MultiplayerSynchronized
import ext.TAG
import ext.fromId
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import sessions.SessionInfo
import sessions.Topic
import ui.components.SessionInfoItem
import util.Log

class SessionViewController(
    private var session: SessionInfo
) : UIController(), MultiplayerSynchronized {

    override val mainGUIName: String = "session_view"

    init {
        ConnectionManager.register(this)
    }

    override suspend fun init() {
        super.init()
        updatePlayers(session)
    }

    private suspend fun updatePlayers(session: SessionInfo) {
        val container = Element.fromId("playerInfoContainer") ?: return
        container.innerHTML = ""
        for (player in session.players) {
            insertComponent(SessionInfoItem(player), container)
        }
    }

    override fun rebuild() {
        Log.d(TAG, "rebuilding")

    }

    override val topic: String = Topic.SESSION_STATE

    override fun onMessage(message: GameMessageData, type: MessageType) {
        when (type) {
            MessageType.SessionStateChange -> {
                if (message is GameMessageData.SessionStateUpdate) {
                    session = message.session
                    MainScope().launch { updatePlayers(session) }
                }
            }
            else -> {}
        }
    }


}