package com.ichbinluka

import com.ichbinluka.plugins.configureRouting
import com.ichbinluka.services.GameSessionServiceImpl
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*

data class ApplicationConfig(
    val discordClientId: String,
    val discordClientSecret: String,
)

fun Application.module(config: ApplicationConfig) {
    configurePlugins()
    val service = GameSessionServiceImpl()
    // configureSockets()
    configureRouting(service, config)
}

fun Application.configurePlugins() {
    install(ContentNegotiation) {
        json()
    }
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Post)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.AccessControlAllowHeaders)
        exposeHeader(HttpHeaders.AccessControlAllowOrigin)
        exposeHeader(HttpHeaders.AccessControlAllowCredentials)
        exposeHeader(HttpHeaders.AccessControlAllowHeaders)

        allowCredentials = true
        anyHost()
    }
}

