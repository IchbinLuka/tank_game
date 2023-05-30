package com.ichbinluka

import com.ichbinluka.plugins.configureRouting
import com.ichbinluka.plugins.configureSockets
import com.ichbinluka.services.GameSessionServiceImpl
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*

fun Application.module() {
    configurePlugins()
    val service = GameSessionServiceImpl()
    configureSockets()
    configureRouting(service)
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
        allowHeader(HttpHeaders.Authorization)
        allowHeader("MyCustomHeader")
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }
}

