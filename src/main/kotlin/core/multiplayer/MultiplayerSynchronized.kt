package core.multiplayer

import GameMessageData
import MessageType

interface MultiplayerSynchronized {
    val topic: String

    fun onMessage(message: GameMessageData, type: MessageType)
}