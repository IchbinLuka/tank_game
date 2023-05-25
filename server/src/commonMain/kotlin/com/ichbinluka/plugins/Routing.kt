package com.ichbinluka.plugins

import com.ichbinluka.routes.sessions
import com.ichbinluka.services.GameSessionService
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Application.configureRouting(service: GameSessionService) {
    install(Resources)
    routing {
        sessions(service)
    }

}

@Serializable
@Resource("/articles")
class Articles(val sort: String? = "new")
