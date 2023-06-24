package com.ichbinluka.routes

import PlayerData
import com.ichbinluka.services.GameSessionService
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import sessions.SessionCreationInfo


fun Route.sessions(sessionService: GameSessionService) {
    route("sessions") {
        get {
            call.respond(sessionService.getPublicSessions().map { it.toInfo() })
        }
        post {
            val info = call.receive<SessionCreationInfo>()
            if (info.name.trim() == "") {
                throw BadRequestException("Invalid name")
            }
            call.respond(sessionService.createSession(info).toInfo())
        }
    }

    route("sessions/{id}/join") {
        post {
            val playerData = call.receive<PlayerData>()
            val id = call.parameters["id"]?.toLongOrNull() ?: throw BadRequestException("Invalid id")
            val session = sessionService.getSessionById(id) ?: throw NotFoundException()
        }
    }

    route("sessions/{id}") {
        delete {

        }
        get {
            val id = call.parameters["id"]?.toLongOrNull() ?: throw BadRequestException("Invalid id")
            call.respond(sessionService.getSessionById(id)?.toInfo() ?: throw NotFoundException())
        }
    }
}