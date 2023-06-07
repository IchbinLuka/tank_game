package com.ichbinluka.routes

import GameMessage
import GameMessageData
import com.ichbinluka.model.Player
import com.ichbinluka.services.GameSessionService
import com.ichbinluka.services.PlayerConnectionService
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration

fun Application.webSocketRoute(
    sessionService: GameSessionService,
) {
    install(WebSockets) {
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        webSocket("session/{id}/connect") {
            // websocketSession
            val id = call.parameters["id"]?.toLong() ?: throw BadRequestException("missing session id in url")
            // val token = call.parameters["token"]?.toLong() ?: throw BadRequestException("missing token in url")
            val message = withTimeoutOrNull(2000L) {
                GameMessage.fromByteArray(incoming.receive().data)
            } ?: return@webSocket

            val messageData = GameMessageData.fromGameMessage(message)
            if (messageData !is GameMessageData.ConnectionRequest) return@webSocket

            val session = sessionService.getSessionById(id) ?: throw NotFoundException("Session not found")
            session.info.password?.let { if (it != messageData.password) return@webSocket }
            val player = Player(messageData.playerData, this)
            session.addPlayer(player)
            outgoing.send(Frame.Binary(true, GameMessageData.ConnectionResponse(
                    successful = true
            ).toByteArray()))

            for (frame in incoming) {
                val message = GameMessage.fromByteArray(frame.data)
                session.broadcast(message, player)
            }
        }
    }
}