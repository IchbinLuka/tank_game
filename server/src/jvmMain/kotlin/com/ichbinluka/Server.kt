package com.ichbinluka

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import sessions.PORT


fun main() {
    embeddedServer(Netty, port = PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}