package gameobjects

import GameMessageData
import MessageType
import core.ConnectionManager
import core.Player
import core.multiplayer.MultiplayerSynchronized
import ext.fromVector3
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

    

    override fun onMessage(message: GameMessageData) {
        when (message) {
            is GameMessageData.TransformationUpdate -> {
                position = message.pos.toVector3()
                rotation = Euler().setFromVector3(message.rot.toVector3())
            }
            is GameMessageData.Death -> {
                die()
            }
            else -> {}
        }
    }

    override fun onDeath() {
        super.onDeath()
        if (proxy) return
        ConnectionManager.sendMessage(GameMessage.fromGameMessageData(GameMessageData.Death, topic))
    }

    override fun move(vector3: Vector3, deltaTime: Double, ground: Ground) {
        super.move(vector3, deltaTime, ground)
        ConnectionManager.sendMessage(
            GameMessage.fromGameMessageData(
                GameMessageData.TransformationUpdate(
                    pos = GameMessageData.Vec3.fromVector3(position),
                    rot = GameMessageData.Vec3.fromVector3(rotation.toVector3())
                ),
                topic
            )
        )
    }
}