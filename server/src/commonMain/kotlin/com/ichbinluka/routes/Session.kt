package com.ichbinluka.routes

import PlayerData
import com.ichbinluka.services.GameSessionService
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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
            call.respond(sessionService.createSession(info, public = true).toInfo())
        }
    }

    post("sessions/discord/{channelId}") {
        val channelId: String = call.parameters["channelId"] ?: throw BadRequestException("Invalid channelId")
        val info = call.receive<SessionCreationInfo>()
        val session = sessionService.getSessionById(channelId)
        if (session != null) {
            call.respond(session.toInfo())
        } else {
            call.respond(sessionService.createSession(info, public = false, id = channelId).toInfo())
        }
    }

    route("sessions/{id}") {
        delete {

        }
        get {
            val id = call.parameters["id"] ?: throw BadRequestException("Invalid id")
            call.respond(sessionService.getSessionById(id)?.toInfo() ?: throw NotFoundException())
        }
    }

    route("sessions/{id}/player_config_available") {
        get {
            val id = call.parameters["id"] ?: throw BadRequestException("Invalid id")
            val data: PlayerData = call.receive()
            call.respond(sessionService.getSessionById(id)?.playerConfigAvailable(data) ?: throw NotFoundException())
        }
    }
}