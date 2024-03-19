@file:OptIn(
    ExperimentalSerializationApi::class, ExperimentalSerializationApi::class,
    ExperimentalSerializationApi::class
)

package services

import APITokenRequest
import APITokenResponse
import ColorData
import PlayerData
import core.Game
import ext.TAG
import ext.fromId
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToDynamic
import org.w3c.dom.Element
import org.w3c.dom.url.URLSearchParams
import org.w3c.fetch.RequestInit
import sessions.DISCORD_CLIENT_ID
import sessions.HTTP_URL
import sessions.SessionCreationInfo
import sessions.SessionInfo
import three.js.Color
import types.DiscordAPIAuth
import types.DiscordSDK
import util.Log
import util.objectOf
import kotlin.random.Random

@Serializable
data class Headers(
    @SerialName("Content-Type")
    val contentType: String = "text/plain",
    @SerialName("Access-Control-Allow-Origin")
    val accessControlAllowOrigin: String = "*",
    @SerialName("Origin")
    val origin: String = "*"
)

object APIHelper {

    val discord = DiscordHelper(DISCORD_CLIENT_ID)

    var host: String?
        get() = Game.instance.settings.value.server
        private set(value) {
            value?.let {
                Game.instance.settings.value = Game.instance.settings.value.copy(server = it)
            }
        }

    val url: String? get() = host?.let { "https://$it" }

    suspend fun updateHost(host: String): Boolean = try {
        val response = window.fetch("https://$host/status").await()
        val successful = response.status == 200.toShort()
        if (successful) {
            this.host = host
        }
        successful
    } catch (e: Throwable) {
        false
    }

    fun resetHost() {
        host = null
    }

    suspend fun serverAvailable(): Boolean = try {
        val response = window.fetch("$url/status").await()
        response.status == 200.toShort()
    } catch (e: Throwable) {
        false
    }

    suspend fun fetchSessions(): Collection<SessionInfo> {
        val response = window.fetch("${url}/sessions").await()
        if (response.status != 200.toShort()) {
            throw IllegalStateException(response.body.toString())
        }
        return Json.decodeFromString<List<SessionInfo>>(response.text().await())
    }

    suspend fun fetchSessionById(id: String): SessionInfo {
        val response = window.fetch("${url}/sessions/${id}").await()
        if (response.status != 200.toShort()) {
            throw IllegalStateException(response.body.toString())
        }
        return Json.decodeFromString<SessionInfo>(response.text().await())
    }

    suspend fun createSession(info: SessionCreationInfo): SessionInfo {
        val response = window.fetch(
            "${url}/sessions", RequestInit(
            method = "POST",
            body = Json.encodeToString(info),
            headers = Json.encodeToDynamic(Headers(contentType = "application/json"))
        )).await()
        when (response.status) {
            200.toShort() -> return Json.decodeFromString(response.text().await())
            409.toShort() -> throw IllegalStateException("Session with name ${info.name} already exists")
            400.toShort() -> throw IllegalStateException("Invalid name")
            else -> throw IllegalStateException(response.body.toString())
        }
    }

    @Suppress("unused")
    private const val TAG = "APIHelper"
}

class DiscordHelper(private val discordClientId: String) {
    private lateinit var discordSDK: DiscordSDK

    var auth: DiscordAPIAuth? = null
        private set


    fun shouldUseDiscord(): Boolean = URLSearchParams(window.location.search).has("instance_id")


    fun getPlayer(): PlayerData = auth?.user?.let {
        PlayerData(
            name = it.global_name,
            color = ColorData(
                Color(r = Random.nextDouble(), g = Random.nextDouble(), b = Random.nextDouble()).getHex().toInt()
            )
        )
    } ?: throw IllegalStateException("Not authenticated with Discord")

    suspend fun createSession(playerData: PlayerData): SessionInfo {
        val response = window.fetch(
            "$HTTP_URL/sessions/discord/${discordSDK.channelId}", RequestInit(
                method = "POST",
                headers = Json.encodeToDynamic(Headers(contentType = "application/json")),
                body = Json.encodeToString(
                    SessionCreationInfo(
                        name = "Discord Session",
                        host = playerData
                    )
                )
            )
        ).await()

        when (response.status) {
            200.toShort() -> return Json.decodeFromString(response.text().await())
            400.toShort() -> throw IllegalStateException("Invalid name")
            else -> throw IllegalStateException(response.body.toString())
        }
    }

    suspend fun setup() {
        discordSDK = DiscordSDK(discordClientId)
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

        Log.d(TAG, "Code: ${JSON.stringify(code)}")

        val tokenResponse = window.fetch(
            "$HTTP_URL/discord_token", RequestInit(
                method = "POST",
                headers = Json.encodeToDynamic(Headers(contentType = "application/json")),
                body = Json.encodeToString(APITokenRequest(code.code as String))
            )
        ).await()
        val accessToken: APITokenResponse = Json.decodeFromString(tokenResponse.text().await())

        Log.d(TAG, "Access token: ${JSON.stringify(accessToken)}")

        val auth = discordSDK.commands.authenticate(objectOf {
            access_token = accessToken.accessToken
        }).await()

        this.auth = auth

        if (auth == null) {
            Log.e(TAG, "Failed to authenticate with Discord")
            return
        }

        setupLocale()

        Log.i(TAG, "Auth: ${JSON.stringify(auth)}")
    }

    private suspend fun setupLocale() {
        val locale = discordSDK.commands.userSettingsGetLocale().await()
        Game.instance.settings.value = Game.instance.settings.value.copy(language = locale.locale)
        Log.d(TAG, "Updated locale to ${JSON.stringify(locale)}")
    }
}