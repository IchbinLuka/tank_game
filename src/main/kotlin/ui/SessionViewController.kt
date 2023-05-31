package ui

import GameMessageData
import MessageType
import core.Game
import core.multiplayer.MultiplayerSynchronized
import ext.fromId
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import sessions.Session
import ui.components.SessionInfoItem

class SessionViewController(
    private var session: Session
) : UIController(), MultiplayerSynchronized {

    override val mainGUIName: String = "session_view"

    init {
        Game.instance.connectionManager.register(this)
    }

    override suspend fun init() {
        super.init()
        updatePlayers(session)
    }

    private suspend fun updatePlayers(session: Session) {
        val container = Element.fromId("playerInfoContainer") ?: return
        container.innerHTML = ""
        for (player in session.players) {
            insertComponent(SessionInfoItem(player), container)
        }
    }

    override fun rebuild() {
        MainScope().launch { updatePlayers(session) }
    }
    override val topic: String = "session_room"

    override fun onMessage(message: GameMessageData, type: MessageType) {
        when (type) {
            MessageType.SessionStateChange -> {
                if (message is GameMessageData.SessionStateUpdate) {
                    session = message.session
                    rebuild()
                }
            }
            else -> {}
        }
    }


}