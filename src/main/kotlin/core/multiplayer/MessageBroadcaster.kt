package core.multiplayer

import GameMessage

interface MessageBroadcaster {
    fun broadcast(message: GameMessage)
}