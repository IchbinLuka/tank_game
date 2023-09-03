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
import sessions.Topic
import three.js.Euler
import three.js.Vector2
import three.js.Vector3
import kotlin.math.max

class MultiplayerTank(
    startPosition: Vector3,
    startRotation: Euler = Euler(),
    player: Player,
    proxy: Boolean = false,
) : Tank(startPosition, player = player, startRotation = startRotation), MultiplayerSynchronized {

    override val topic: String = Topic.tank(player.name)

    private val broadcaster = RateLimitedBroadcaster(10, ConnectionManager)
    private val urgentBroadcaster = ConnectionManager

    override val invincible: Boolean = true


    private fun getTransformationUpdate(): GameMessageData.TransformationUpdate = GameMessageData.TransformationUpdate(
        pos = GameMessageData.Vec3.fromVector3(position),
        rot = GameMessageData.Vec3.fromVector3(rotation.toVector3())
    )

    init {
        ConnectionManager.register(this)
        if (!proxy) {
            addListener { _, _, _ ->
                urgentBroadcaster.broadcast(
                    GameMessage.fromGameMessageData(
                        GameMessageData.BulletSelectionUpdate(currentBullet),
                        topic
                    )
                )
            }
        }
    }

    override fun createBulletArgs(velocity: Double): BulletObjectArgs {
        return super.createBulletArgs(velocity).copy(synchronized = true, proxy = false)
    }

    override fun onMessage(message: GameMessageData) {
        when (message) {
            is GameMessageData.TransformationUpdate -> {
                position = message.pos.toVector3()
                rotation = Euler().setFromVector3(message.rot.toVector3())
            }

            is GameMessageData.Death -> {
                damage(1.0)
            }

            is GameMessageData.CannonUpdateData -> {
                cannon?.rotation?.z = message.angle
                setDirection(message.direction)
            }

            is GameMessageData.PlayerHealthUpdate -> {
                if (!active) return
                health = max(0.0, message.newHealth)
                if (health == 0.0) {
                    onDeath()
                }
            }

            is GameMessageData.BulletSelectionUpdate -> {
                currentBullet = message.bullet
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

    override fun adjustTransformToGround(ground: Ground) {
        super.adjustTransformToGround(ground)
        broadcaster.broadcast(
            GameMessage.fromGameMessageData(
                getTransformationUpdate(),
                topic
            )
        )
    }
}