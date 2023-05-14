package com.ichbinluka

import com.ichbinluka.plugins.configureRouting
import com.ichbinluka.plugins.configureSockets
import com.ichbinluka.services.GameSessionService
import com.ichbinluka.services.GameSessionServiceImpl
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    val service = GameSessionServiceImpl()
    configureKoin()
    configureSockets()
    configureRouting(service)
}

fun configureKoin() {
    startKoin {
        modules
    }
}

val modules = module {
    single<GameSessionService> {
        GameSessionServiceImpl()
    }
}
