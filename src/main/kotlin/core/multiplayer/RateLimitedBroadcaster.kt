package core.multiplayer

import GameMessage
import core.ConnectionManager
import three.js.Clock

/**
 * Rate limiter for sending messages
 */
class RateLimitedBroadcaster(
    maxRate: Int,
    private val sender: MessageBroadcaster = ConnectionManager
) : MessageBroadcaster {
    private val maxTime = 1 / maxRate
    private val clock = Clock().apply { start() }

    override fun broadcast(message: GameMessage) {
        if (clock.getDelta().toDouble() > maxTime) {
            sender.broadcast(message)
        }
    }
}