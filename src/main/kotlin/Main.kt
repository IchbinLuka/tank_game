@file:OptIn(ExperimentalSerializationApi::class)

import core.Game
import ext.fromId
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToDynamic
import org.w3c.dom.Element
import org.w3c.fetch.RequestInit
import scenes.StartScene
import services.Headers
import sessions.DISCORD_CLIENT_ID
import sessions.HTTP_URL
import types.DiscordSDK
import util.GUIHelper
import util.Log
import util.objectOf

fun main() {
    MainScope().launch {
        setupDiscordSDK()
    }
    val game = Game(StartScene())
    GUIHelper.initListeners() // TODO: this call should be unnecessary
    game.mainLoop()
}

suspend fun setupDiscordSDK() {
    val discordSDK = DiscordSDK(DISCORD_CLIENT_ID)
    discordSDK.ready().await()
    Element.fromId("cssObjectsContainer")?.innerHTML =
        "Discord SDK ready. Guild ID: ${discordSDK.guildId}, Channel ID: ${discordSDK.channelId}"

    val code = discordSDK.commands.authorize(objectOf {
        client_id = DISCORD_CLIENT_ID
        response_type = "code"
        state = ""
        prompt = "none"
        scope = Json.encodeToDynamic(listOf("identify", "guilds"))
    }).await()

    Log.d("Main", "Code: ${JSON.stringify(code)}")

    val tokenResponse = window.fetch(
        "$HTTP_URL/discord_token", RequestInit(
            method = "POST",
            headers = Json.encodeToDynamic(Headers(contentType = "application/json")),
            body = Json.encodeToString(APITokenRequest(code.code as String))
        )
    ).await()
    val accessToken: APITokenResponse = Json.decodeFromString(tokenResponse.text().await())

    Log.d("Main", "Access token: ${JSON.stringify(accessToken)}")

    val auth = discordSDK.commands.authenticate(objectOf {
        access_token = accessToken.accessToken
    })

    Log.i("Main", "Auth: ${JSON.stringify(auth)}")
}