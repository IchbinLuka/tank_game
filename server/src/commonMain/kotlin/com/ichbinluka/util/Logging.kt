package com.ichbinluka.util

import io.ktor.server.application.*
import io.ktor.util.logging.*

val ApplicationCallPipeline.log: Logger
    get() = environment!!.log