package com.ichbinluka.plugins

import com.ichbinluka.ApplicationConfig
import com.ichbinluka.routes.discordSdk
import com.ichbinluka.routes.sessions
import com.ichbinluka.routes.status
import com.ichbinluka.services.GameSessionService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(service: GameSessionService, config: ApplicationConfig) {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }

    install(Resources)
    routing {
        sessions(service)
        status()
        discordSdk(config.discordClientId, config.discordClientSecret)


    }

}