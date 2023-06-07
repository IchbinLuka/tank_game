package com.ichbinluka.services

import GameMessage
import PlayerData

interface WebSocketConnectionService {
    fun broadcastMessage(player: PlayerData, message: GameMessage)
}