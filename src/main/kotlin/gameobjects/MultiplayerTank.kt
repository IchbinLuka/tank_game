package gameobjects

import GameMessage
import GameMessageData
import core.ConnectionManager
import core.Player
import core.multiplayer.MultiplayerSynchronized
import core.multiplayer.RateLimitedBroadcaster
import ext.fromVector3
import ext.toVector3
import gameobjects.bullets.BulletObjectArgs
import three.js.Euler
import three.js.Vector2
import three.js.Vector3

class MultiplayerTank(
    startPosition: Vector3,
    startRotation: Euler = Euler(),
    player: Player,
    private val proxy: Boolean = false,
) : Tank(startPosition, player = player, startRotation = startRotation), MultiplayerSynchronized {

    override val topic: String = player.name

    private val broadcaster = RateLimitedBroadcaster(10, ConnectionManager)
    private val urgentBroadcaster = ConnectionManager


    init {
        ConnectionManager.register(this)
    }

    override fun createBulletArgs(velocity: Double): BulletObjectArgs {
        return super.createBulletArgs(velocity).copy(synchronized = true)
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

            is GameMessageData.CannonUpdateData -> {
                cannon?.rotation?.z = message.angle
                setDirection(message.direction)
            }

            else -> {}
        }
    }

    override fun aim(point: Vector2) {
        super.aim(point)
        broadcaster.broadcast(
            GameMessage.fromGameMessageData(
                GameMessageData.CannonUpdateData(
                    angle = cannon?.rotation?.z?.toDouble() ?: 0.0,
                    direction = direction
                ),
                topic
            )
        )
    }

    override fun onDeath() {
        super.onDeath()
        if (proxy) return
        urgentBroadcaster.broadcast(GameMessage.fromGameMessageData(GameMessageData.Death, topic))
    }

    override fun move(vector3: Vector3, deltaTime: Double, ground: Ground) {
        super.move(vector3, deltaTime, ground)
        broadcaster.broadcast(
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