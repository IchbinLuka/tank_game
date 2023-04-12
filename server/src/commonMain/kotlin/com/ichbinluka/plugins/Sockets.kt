package com.ichbinluka.plugins

import GameMessage
import GameMessageData
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*

fun Application.configureSockets() {
    install(WebSockets) {
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        webSocket("/ws") { // websocketSession
            for (frame in incoming) {
                val message = GameMessage.fromByteArray(frame.data)
                when (val messageData = GameMessageData.fromGameMessage(message)) {
                    is GameMessageData.Vec3Data -> outgoing.send(Frame.Text("You sent position: $messageData on ${message.topic}"))
                    is GameMessageData.Empty -> outgoing.send(Frame.Text("You sent an empty message"))
                    else -> outgoing.send(Frame.Text("You sent an unknown message"))
                }
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    outgoing.send(Frame.Text("YOU SAID: $text"))
                    if (text.equals("bye", ignoreCase = true)) {
                        close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                    }
                }
            }
        }
    }
}
