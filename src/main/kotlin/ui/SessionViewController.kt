package ui

import GameMessage
import GameMessageData
import PlayerData
import core.ConnectionManager
import core.Game
import core.multiplayer.MultiplayerSynchronized
import ext.TAG
import ext.fromId
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
import scenes.LoadingScene
import scenes.MainSceneMultiplayer
import sessions.SessionInfo
import sessions.Topic
import ui.views.menuButton
import ui.views.menuElement
import ui.views.menuPage
import ui.views.sessionItem
import util.GUIHelper
import util.Log

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
                            Log.d(TAG, "Players: ${session.players}")
                            for (player in session.players) {
                                sessionItem(player)
                            }
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

                            disabled = session.players.size < 2
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
        updateSession(session)
    }

    private fun startGame() {
        ConnectionManager.broadcast(
            GameMessage.fromGameMessageData(
                GameMessageData.GameStartRequest,
                Topic.GLOBAL
            )
        )
    }

    private fun updateSession(session: SessionInfo) {
        this.session = session
        GUIHelper.refresh()
    }

    override val topic: String = Topic.SESSION_STATE

    override fun onMessage(message: GameMessageData) {
        when (message) {
            is GameMessageData.SessionInfoUpdate -> {
                updateSession(message.session)
            }
            is GameMessageData.GameStartData -> {
                ConnectionManager.unregister(this)
                if (Game.instance.currentScene !is LoadingScene) {
                    Game.instance.currentScene = MainSceneMultiplayer(message, self)
                }
            }
            is GameMessageData.Disconnect -> {
                MainScope().launch { GUIHelper.pop() }
            }
            else -> {}
        }
    }


}