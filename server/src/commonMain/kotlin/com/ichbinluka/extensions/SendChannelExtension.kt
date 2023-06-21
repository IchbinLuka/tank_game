package com.ichbinluka.extensions

import GameMessage
import io.ktor.websocket.*
import kotlinx.coroutines.channels.SendChannel

suspend fun SendChannel<Frame>.send(message: GameMessage) {
    send(Frame.Binary(true, message.toByteArray()))
}