package ui

import GameMessageData
import core.ConnectionManager
import core.Game
import core.multiplayer.MultiplayerSynchronized
import ext.fromId
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import scenes.MultiplayerGameScene
import sessions.SessionInfo
import sessions.Topic
import ui.components.SessionInfoItem
import util.GUIHelper

class SessionViewController(
    private var session: SessionInfo
) : UIController(), MultiplayerSynchronized {

    override val mainGUIName: String = "session_view"


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
    }

    override fun rebuild() {

    }

    override val topic: String = Topic.SESSION_STATE

    override fun onMessage(message: GameMessageData) {
        when (message) {
            is GameMessageData.SessionStateUpdate -> {
                session = message.session
                MainScope().launch { updatePlayers(session) }
            }
            is GameMessageData.GameStartData -> {
                Game.instance.currentScene = MultiplayerGameScene(message)
            }
            is GameMessageData.Disconnect -> {
                MainScope().launch { GUIHelper.pop() }
            }
            else -> {}
        }
    }


}