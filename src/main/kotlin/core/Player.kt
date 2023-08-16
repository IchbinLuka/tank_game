package core

import Bullet
import GameMessageData
import PlayerData
import core.multiplayer.MultiplayerSynchronized
import ext.toColor
import sessions.Topic
import three.js.Color
import util.ValueNotifier

class Player(
    val color: Color,
    override val name: String,
    startInventory: Map<Bullet, Int?> = mapOf()
) : MultiplayerSynchronized {
    val inventory: MutableMap<Bullet, Int?> = hashMapOf<Bullet, Int?>(
        Bullet.Normal to null
    )

    private val healthNotifier = ValueNotifier(1.0)
    var health: Double by healthNotifier

    init {
        inventory.putAll(startInventory)
    }

    override val topic: String = Topic.player(name)

    override fun onMessage(message: GameMessageData) {
        when (message) {
            is GameMessageData.PlayerHealthUpdate -> {
                health = message.newHealth
            }

            is GameMessageData.BulletCounts -> {
                inventory.putAll(message.getCountsMap())
            }
            else -> {}
        }
    }

    companion object {
        fun fromPlayerData(playerData: PlayerData): Player = Player(playerData.color.toColor(), playerData.name)

        fun fromTankData(tankData: GameMessageData.TankData): Player =
            Player(tankData.player.color.toColor(), tankData.player.name, tankData.bulletCounts.getCountsMap())
    }
}