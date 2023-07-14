package core.multiplayer

import GameMessageData
import core.gameobjects.Nameable

interface MultiplayerSynchronized: Nameable {
    val topic: String

    fun onMessage(message: GameMessageData)
}