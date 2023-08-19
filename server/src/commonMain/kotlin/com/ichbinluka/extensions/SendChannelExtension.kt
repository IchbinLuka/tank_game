package com.ichbinluka.extensions

import GameMessage
import GameMessageData
import io.ktor.websocket.*
import kotlinx.coroutines.channels.SendChannel

suspend fun SendChannel<Frame>.send(message: GameMessage) {
    send(Frame.Binary(true, message.toByteArray()))
}

suspend inline fun <reified T : GameMessageData> SendChannel<Frame>.send(message: T, topic: String) =
    send(GameMessage.fromGameMessageData<T>(message, topic))