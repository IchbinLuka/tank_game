package com.ichbinluka.services

import PlayerData
import com.ichbinluka.model.Player
import io.ktor.websocket.*

interface PlayerConnectionService {
    fun getPlayer(token: Long): PlayerData?
    fun addPlayer(token: Long, player: PlayerData)

    fun removePlayer(token: Long)

    fun connectPlayer(token: Long, wsSession: WebSocketSession): Player
}