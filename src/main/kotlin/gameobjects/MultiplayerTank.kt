package gameobjects

import GameMessageData
import MessageType
import core.ConnectionManager
import core.Player
import core.multiplayer.MultiplayerSynchronized
import ext.toVector3
import three.js.Euler
import three.js.Vector3

class MultiplayerTank(
    startPosition: Vector3,
    startRotation: Euler = Euler(),
    player: Player,
    private val proxy: Boolean = false,
) : Tank(startPosition, player = player, startRotation = startRotation), MultiplayerSynchronized {
    override val topic: String = player.name


    init {
        if (proxy) {
            ConnectionManager.register(this)
        }
    }

    override fun onMessage(message: GameMessageData, type: MessageType) {
        when (type) {
            MessageType.GameObjectMoveUpdate -> {
                message as GameMessageData.GameObjectMoveUpdate
                position = message.pos.toVector3()
                rotation = Euler().setFromVector3(message.rot.toVector3())
            }
            else -> {}
        }
    }
}