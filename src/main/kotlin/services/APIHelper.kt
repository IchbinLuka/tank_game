@file:OptIn(ExperimentalSerializationApi::class, ExperimentalSerializationApi::class)

package services

import core.Game
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToDynamic
import org.w3c.fetch.RequestInit
import sessions.SessionCreationInfo
import sessions.SessionInfo

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

    suspend fun fetchSessionById(id: Long): SessionInfo {
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