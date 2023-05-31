package com.ichbinluka.routes

import PlayerData
import com.ichbinluka.services.GameSessionService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class SessionCreationInfo(
    val name: String,
    val host: PlayerData,
    val password: String? = null
)

fun Route.sessions(sessionService: GameSessionService) {
    route("sessions") {
        get {
            call.respond(sessionService.getPublicSessions())
        }
        post {
            val info = call.receive<SessionCreationInfo>()
            sessionService.createSession(info)
            call.respond(info)
        }
    }

    route("sessions/{id}") {
        delete {

        }
        get {

        }
    }
}