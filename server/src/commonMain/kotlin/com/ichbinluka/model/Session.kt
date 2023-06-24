package com.ichbinluka.model

import GameMessage
import GameMessageData
import PlayerData
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedSendChannelException
import sessions.SessionInfo
import sessions.Topic


class Session(
    val info: SessionInfo,
) {
    val players = mutableListOf<Player>()

    var host: PlayerData = info.host

    fun addPlayer(player: Player) {
        players.add(player)
    }

    fun removePlayer(player: Player) {
        players.remove(player)
    }

    suspend fun broadcast(message: GameMessage, player: Player? = null) {
        val iter = players.iterator()
        var playersChanged = false
        val messageBytes = message.toByteArray()
        for (p in iter) {
            if (p.id == player?.id) continue // Skip broadcaster
            try {
                p.session.send(messageBytes)
            } catch (e: ClosedSendChannelException) {
                iter.remove()
                playersChanged = true
            }
        }
        if (playersChanged) {
            broadcastUpdate()
        }
    }

    suspend fun broadcastUpdate() {
        broadcast(
            GameMessage.fromGameMessageData<GameMessageData.SessionStateUpdate>(
                GameMessageData.SessionStateUpdate(toInfo()),
                Topic.SESSION_STATE
            ),
            null
        )
    }

    fun toInfo(): SessionInfo = info.copy(players = players.map { it.data }, host = host)

    fun isEmpty(): Boolean = players.isEmpty()
}