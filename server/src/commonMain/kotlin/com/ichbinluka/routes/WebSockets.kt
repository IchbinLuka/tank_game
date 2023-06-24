@file:OptIn(ExperimentalSerializationApi::class)

package com.ichbinluka.routes

import GameMessage
import GameMessageData
import MessageType
import com.benasher44.uuid.uuid4
import com.ichbinluka.model.Player
import com.ichbinluka.services.GameSessionService
import createGame
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import sessions.Topic
import sessions.WEBSOCKET_ROUTE

fun Application.webSocketRoute(
    sessionService: GameSessionService,
) {
    install(WebSockets) {
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        webSocket(WEBSOCKET_ROUTE) {
            val message = withTimeoutOrNull(2000L) {
                GameMessage.fromByteArray(incoming.receive().data)
            } ?: return@webSocket

            val messageData = GameMessageData.fromGameMessage(message)
            if (messageData !is GameMessageData.ConnectionRequest) return@webSocket

            val session =
                sessionService.getSessionById(messageData.sessionId) ?: throw NotFoundException("Session not found")
            session.info.password?.let { if (it != messageData.password) return@webSocket }
            val token = uuid4().leastSignificantBits
            val player = Player(messageData.playerData, token, this)
            session.addPlayer(player)
            send(
                Frame.Binary(
                    true, GameMessage(
                        Topic.CONNECTION_RESPONSE,
                        MessageType.ConnectionResponse,
                        ProtoBuf.encodeToByteArray(
                            GameMessageData.ConnectionResponse(
                                successful = true,
                                authToken = token
                            )
                        )
                    ).toByteArray()
                )
            )


            session.broadcast(
                GameMessage.fromGameMessageData<GameMessageData.SessionStateUpdate>(
                    GameMessageData.SessionStateUpdate(session.toInfo()),
                    Topic.SESSION_STATE
                ),
                null
            )

            for (frame in incoming) {
                val message = GameMessage.fromByteArray(frame.data)
                val messageData = GameMessageData.fromGameMessage(message)
                when (messageData) {
                    is GameMessageData.Disconnect -> {
                        session.removePlayer(player)
                        if (session.isEmpty()) {
                            sessionService.closeSession(session.toInfo().id)
                        } else {
                            if (player.data == session.info.host) {
                                session.host = player.data
                            }
                            session.broadcastUpdate()
                        }
                    }
                    is GameMessageData.GameStartRequest -> {
                        if (player.data == session.info.host) {
                            println("Got start request")
                            session.broadcast(
                                GameMessage.fromGameMessageData(
                                    data = createGame(session.toInfo().players),
                                    topic = Topic.SESSION_STATE
                                )
                            )
                        }
                    }
                    else -> {
                        session.broadcast(message, player)
                    }
                }
            }
        }
    }
}