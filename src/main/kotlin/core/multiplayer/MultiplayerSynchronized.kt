package core.multiplayer

import GameMessageData

interface MultiplayerSynchronized {
    val topic: String

    fun onMessage(message: GameMessageData)
}