package com.ichbinluka.services

import PlayerData
import com.ichbinluka.model.Player
import io.ktor.websocket.*

/*
class PlayerConnectionServiceImpl: PlayerConnectionService {
    private val playerTokens = hashMapOf<Long, PlayerData>()
    private val playerConnections = hashMapOf<Long, WebSocketSession>()

    override fun getPlayer(token: Long): PlayerData? = playerTokens[token]

    override fun addPlayer(token: Long, player: PlayerData) {
        playerTokens[token] = player
    }

    override fun removePlayer(token: Long) {
        playerTokens.remove(token)
    }

    override fun connectPlayer(token: Long, wsSession: WebSocketSession): Player {
        playerConnections[token] = wsSession
    }
}*/