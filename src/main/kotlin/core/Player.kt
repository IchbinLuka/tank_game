package core

import GameMessageData
import MessageType
import PlayerData
import core.multiplayer.MultiplayerSynchronized
import ext.toColor
import gameobjects.bullets.Bullet
import three.js.Color

class Player(
    val color: Color,
    val name: String,
    startInventory: Map<Bullet, UInt?> = mapOf()
) : MultiplayerSynchronized {
    val inventory: MutableMap<Bullet, Int?> = hashMapOf<Bullet, Int?>(
        Bullet.Normal to null
    )
    var health: Double = 1.0

    init {
        inventory.putAll(startInventory.mapValues { it.value?.toInt() })
    }

    override val topic: String = "player_$name"

    override fun onMessage(message: GameMessageData, type: MessageType) {
        when (type) {
            MessageType.PlayerHealthUpdate -> {
                message as GameMessageData.PlayerHealthUpdate
                health = message.newHealth
            }
            else -> {}
        }
    }

    companion object {
        fun fromPlayerData(playerData: PlayerData): Player = Player(playerData.color.toColor(), playerData.name)
    }
}