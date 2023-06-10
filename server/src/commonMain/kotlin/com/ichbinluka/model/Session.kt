package com.ichbinluka.model

import GameMessage
import io.ktor.websocket.*
import sessions.SessionInfo


class Session(
    val info: SessionInfo,
) {
    private val players = mutableListOf<Player>()

    fun addPlayer(player: Player) {
        players.add(player)
    }

    fun removePlayer(player: Player) {
        players.remove(player)
    }

    suspend fun broadcast(message: GameMessage, player: Player?) {
        for (p in players) {
            if (p.id == player?.id) continue
            p.session.outgoing.send(Frame.Binary(true, message.toByteArray()))
        }
    }

    fun toInfo(): SessionInfo = info.copy(players = players.map { it.data })
}