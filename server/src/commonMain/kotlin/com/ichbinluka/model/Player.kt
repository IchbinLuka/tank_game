package com.ichbinluka.model

import PlayerData
import io.ktor.websocket.*

class Player(
    val data: PlayerData,
    val id: Long,
    val session: WebSocketSession
)