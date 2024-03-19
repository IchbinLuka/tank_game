package com.ichbinluka.plugins

import com.ichbinluka.ApplicationConfig
import com.ichbinluka.routes.discordSdk
import com.ichbinluka.routes.sessions
import com.ichbinluka.routes.status
import com.ichbinluka.routes.webSocketRoute
import com.ichbinluka.services.GameSessionService
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*

fun Application.configureRouting(service: GameSessionService, config: ApplicationConfig) {
    install(Resources)
    routing {
        sessions(service)
        status()
        discordSdk(config.discordClientId, config.discordClientSecret)
    }
    webSocketRoute(service)
}