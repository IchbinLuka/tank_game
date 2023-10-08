package ui

import GameMessage
import GameMessageData
import PlayerData
import core.ConnectionManager
import core.Game
import core.multiplayer.MultiplayerSynchronized
import ext.fromId
import ext.replace
import ext.s
import ext.tryCast
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import kotlinx.html.style
import org.w3c.dom.Element
import org.w3c.dom.HTMLButtonElement
import scenes.MainSceneMultiplayer
import sessions.SessionInfo
import sessions.Topic
import ui.views.menuButton
import ui.views.menuElement
import ui.views.menuPage
import ui.views.sessionItem
import util.GUIHelper

class SessionViewController(
    private var session: SessionInfo,
    private val self: PlayerData
) : UIController(), MultiplayerSynchronized {

    override val name: String = "session_view_controller"

    override val mainUIBuilder: UIBuilder = {
        menuPage(
            title = { s("waiting_room") },
            sections = listOf(
                {
                    div {
                        menuElement {
                            id = "playerInfoContainer"
                        }
                    }
                },
                {
                    div {
                        menuButton {
                            style = "visibility: ${if (session.host == self) "visible" else "hidden"}"
                            id = "startButton"
                            s("start_game")

                            onClickFunction = { startGame() }
                        }
                    }
                }
            )
        )
    }

    private val startButton get() = Element.fromId("startButton")?.tryCast<HTMLButtonElement>()


    override suspend fun init() {
        ConnectionManager.register(this)
        super.init()
        updatePlayers(session)
    }

    private fun startGame() {
        ConnectionManager.broadcast(
            GameMessage.fromGameMessageData(
                GameMessageData.GameStartRequest,
                Topic.GLOBAL
            )
        )
    }

    private fun updatePlayers(session: SessionInfo) {
        val container = Element.fromId("playerInfoContainer") ?: return
        container.innerHTML = ""
        container.replace {
            for (player in session.players) {
                sessionItem(player)
            }
        }
        startButton?.disabled = session.players.size < 2
    }

    override val topic: String = Topic.SESSION_STATE

    override fun onMessage(message: GameMessageData) {
        when (message) {
            is GameMessageData.SessionInfoUpdate -> {
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