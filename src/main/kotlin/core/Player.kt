package core

import Bullet
import GameMessage
import GameMessageData
import PlayerData
import core.multiplayer.MultiplayerSynchronized
import core.multiplayer.RateLimitedBroadcaster
import ext.TAG
import ext.toColor
import sessions.Topic
import three.js.Color
import util.Log
import util.ValueNotifier

class Player(
    val color: Color,
    override val name: String,
    startInventory: Map<Bullet, Int?> = mapOf(),
    synchronizeUpdates: Boolean = false
) : MultiplayerSynchronized {
    val inventory: MutableMap<Bullet, Int?> = hashMapOf<Bullet, Int?>(
        Bullet.Normal to null
    )

    private val healthNotifier = ValueNotifier(1.0)
    var health: Double by healthNotifier

    val fuelNotifier: ValueNotifier<Double> = ValueNotifier(1.0)
    var fuel: Double by fuelNotifier

    private val broadcaster = RateLimitedBroadcaster(10, ConnectionManager)

    override val topic: String = Topic.player(name)

    init {
        inventory.putAll(startInventory)

        ConnectionManager.register(this)
        if (synchronizeUpdates) {
//            healthNotifier.addListener {
//                ConnectionManager.broadcast(
//                    GameMessage.fromGameMessageData(
//                        GameMessageData.PlayerHealthUpdate(
//                            it
//                        ), topic
//                    )
//                )
//            }

            fuelNotifier.addListener {
                broadcaster.broadcast(
                    GameMessage.fromGameMessageData(
                        GameMessageData.PlayerFuelUpdate(
                            it
                        ), topic
                    )
                )
            }
        }
    }

    override fun onMessage(message: GameMessageData) {
        when (message) {
            is GameMessageData.PlayerHealthUpdate -> {
                health = message.newHealth
            }

            is GameMessageData.BulletCounts -> {
                inventory.putAll(message.getCountsMap())
            }

            is GameMessageData.PlayerFuelUpdate -> {
                fuel = message.fuel
                Log.d(TAG, "Updated fuel to $fuel")
            }

            else -> {}
        }
    }

    companion object {
        fun fromPlayerData(playerData: PlayerData): Player = Player(playerData.color.toColor(), playerData.name)

        fun fromTankData(tankData: GameMessageData.TankData, synchronizeUpdates: Boolean = false): Player =
            Player(
                tankData.player.color.toColor(),
                tankData.player.name,
                tankData.bulletCounts.getCountsMap(),
                synchronizeUpdates = synchronizeUpdates
            )
    }
}