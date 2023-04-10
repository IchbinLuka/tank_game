package com.ichbinluka

import com.ichbinluka.plugins.configureRouting
import com.ichbinluka.plugins.configureSockets
import com.ichbinluka.services.GameSessionService
import com.ichbinluka.services.GameSessionServiceImpl
import io.ktor.server.application.*
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun Application.module() {
    configureKoin()
    configureSockets()
    configureRouting()
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
