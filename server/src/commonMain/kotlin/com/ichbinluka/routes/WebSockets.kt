package com.ichbinluka.routes

import ConnectionResponseStatus
import GameMessage
import GameMessageData
import com.benasher44.uuid.uuid4
import com.ichbinluka.exceptions.MissingBulletException
import com.ichbinluka.extensions.send
import com.ichbinluka.model.Player
import com.ichbinluka.model.SessionStatus
import com.ichbinluka.services.GameSessionService
import com.ichbinluka.util.log
import createGame
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.withTimeoutOrNull
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
            val firstMessage = withTimeoutOrNull(2000L) {
                GameMessage.fromByteArray(incoming.receive().data)
            } ?: return@webSocket

            pingIntervalMillis = 1000L

            val requestMessageData = GameMessageData.fromGameMessage(firstMessage)
            if (requestMessageData !is GameMessageData.ConnectionRequest) return@webSocket

            val session =
                sessionService.getSessionById(requestMessageData.sessionId)
                    ?: throw NotFoundException("Session not found")
            session.toInfo().password?.let { if (it != requestMessageData.password) return@webSocket }
            if (!session.playerConfigAvailable(requestMessageData.playerData)) {
                outgoing.send(
                    GameMessageData.ConnectionResponse(
                        successful = ConnectionResponseStatus.PlayerConfigUnavailable,
                    ),
                    Topic.CONNECTION_RESPONSE
                )
                return@webSocket
            }


            val token = uuid4().leastSignificantBits
            val player = Player(requestMessageData.playerData, token, this)
            session.addPlayer(player)

            outgoing.send(
                GameMessageData.ConnectionResponse(
                    successful = ConnectionResponseStatus.Success,
                    authToken = token
                ),
                Topic.CONNECTION_RESPONSE
            )

            session.broadcast(
                GameMessage.fromGameMessageData<GameMessageData.SessionInfoUpdate>(
                    GameMessageData.SessionInfoUpdate(session.toInfo()),
                    Topic.SESSION_STATE
                ),
                null
            )



            try {
                for (frame in incoming) {
                    val message = GameMessage.fromByteArray(frame.data)
                    val messageData = GameMessageData.fromGameMessage(message)
                    log.debug("Received message: $messageData on topic ${message.topic}")
                    when (messageData) {
                        is GameMessageData.BulletCreationData -> {
                            try {
                                session.handleBulletSpawn(messageData)
                                session.broadcast(message, player)
                            } catch (e: MissingBulletException) {
                                outgoing.send(
                                    Frame.Binary(
                                        true, GameMessage.fromGameMessageData(
                                            GameMessageData.MessageRefusal(
                                                "You don't have any bullets of that type",
                                                message
                                            ),
                                            message.topic
                                        ).toByteArray()
                                    )
                                )
                            }
                        }

                        is GameMessageData.Disconnect -> {
                            session.handleDisconnect(player)
                            if (session.isEmpty()) {
                                sessionService.closeSession(session.toInfo().id)
                            } else {
                                if (player.data == session.host) {
                                    session.host = session.players.first().data
                                }
                                session.broadcastUpdate()
                            }
                        }

                        is GameMessageData.GameStartRequest -> {
                            if (player.data == session.host && session.players.size > 1) {
                                val startData = createGame(session.toInfo().players)
                                session.begin(startData)
                                session.broadcast(
                                    GameMessage.fromGameMessageData(
                                        data = startData,
                                        topic = Topic.SESSION_STATE
                                    )
                                )
                            } else {
                                outgoing.send(
                                    GameMessageData.MessageRefusal(
                                        message = message,
                                        reason = "Not allowed to start game"
                                    ),
                                    message.topic
                                )
                            }
                        }

                        is GameMessageData.BulletImpact, GameMessageData.BulletDespawn -> {
                            session.broadcast(message, player)

                            if (messageData is GameMessageData.BulletImpact) {
                                session.handleBulletImpact(messageData)
                            }
                            session.nextTurn()
                            if (session.status == SessionStatus.FINISHED) break // Game is over → close connection
                        }

                        is GameMessageData.PingRequest -> {
                            outgoing.send(
                                Frame.Binary(
                                    true, GameMessage.fromGameMessageData<GameMessageData.PingResponse>(
                                        GameMessageData.PingResponse,
                                        message.topic
                                    ).toByteArray()
                                )
                            )
                        }

                        is GameMessageData.Death -> {
                            player.health = 0.0
                            session.broadcast(message, player)
                        }

                        is GameMessageData.TransformationUpdate -> {
                            if (message.topic == Topic.tank(player.data.name)) {
                                log.debug("Updating position of $player.data.name} to ${messageData.pos}")
                                player.position = messageData.pos
                                player.rotation = messageData.rot
                                if (player.position.y < 0 && player.alive) {
                                    player.health = 0.0
                                    session.updateDeadPlayers()
                                    if (session.awaitingShot && session.currentTurn == player) {
                                        session.nextTurn()
                                    } else {
                                        session.broadcast(
                                            GameMessage.fromGameMessageData(
                                                session.getState(),
                                                Topic.MAIN_SCENE
                                            )
                                        )
                                    }
                                }
                            }
                            session.broadcast(message, player)
                        }

                        else -> {
                            session.broadcast(message, player)
                        }

                    }
                    if (session.status == SessionStatus.FINISHED) break // Game is over → close connection
                }
            } catch (e: Throwable) {
                log.error("Error in session ${session.toInfo().id}", e)
            } finally {
                session.handleDisconnect(player)
                if (session.isEmpty()) {
                    sessionService.closeSession(session.toInfo().id)
                } else {
                    if (player.data == session.host) {
                        session.host = session.players.first().data
                        session.broadcastUpdate()
                    }
                }
            }
        }
    }
}