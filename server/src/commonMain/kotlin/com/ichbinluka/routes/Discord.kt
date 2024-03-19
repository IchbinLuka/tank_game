@file:OptIn(InternalAPI::class)

package com.ichbinluka.routes

import APITokenRequest
import APITokenResponse
import getHttpClient
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.util.logging.*
import kotlinx.serialization.json.Json

private val log = KtorSimpleLogger("Discord")

val client = getHttpClient()

@OptIn(InternalAPI::class)
fun Route.discordSdk(
    clientId: String,
    clientSecret: String
) {
    post("discord_token") {
        val request = call.receive<APITokenRequest>()

        log.debug("Received request: $request")

        val response = client.post("https://discord.com/api/oauth2/token") {
            body = "client_id=$clientId&grant_type=authorization_code&code=${request.code}&client_secret=$clientSecret"
            headers {
                append("Content-Type", "application/x-www-form-urlencoded")
            }
        }

        log.debug("Body: ${response.bodyAsText()}")

        val token: APITokenResponse = Json.decodeFromString(response.bodyAsText())

        call.respond(token)
    }
}