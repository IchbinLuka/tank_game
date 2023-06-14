@file:OptIn(ExperimentalSerializationApi::class)

package services

import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToDynamic
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import sessions.HTTP_URL
import sessions.SessionCreationInfo
import sessions.SessionInfo

@Serializable
data class Headers(
    @SerialName("Content-Type")
    val contentType: String = "text/plain",
    @SerialName("Access-Control-Allow-Origin")
    val accessControlAllowOrigin: String = "*"
)

object APIHelper {

    suspend fun fetchSessions(): Collection<SessionInfo> {
        val response = window.fetch("${HTTP_URL}/sessions").await()
        if (response.status != 200.toShort()) {
            throw IllegalStateException(response.body.toString())
        }
        return Json.decodeFromString<List<SessionInfo>>(response.text().await())
    }

    suspend fun fetchSessionById(id: Long): SessionInfo {
        val response = window.fetch("${HTTP_URL}/sessions/${id}").await()
        if (response.status != 200.toShort()) {
            throw IllegalStateException(response.body.toString())
        }
        return Json.decodeFromString<SessionInfo>(response.text().await())
    }

    suspend fun createSession(info: SessionCreationInfo): SessionInfo {
        Headers()
        val response = window.fetch("${HTTP_URL}/sessions", RequestInit(
            method = "POST",
            body = Json.encodeToString(info),
            headers = Json.encodeToDynamic(Headers(contentType = "application/json"))
        )).await()
        if (response.status != 200.toShort()) {
            throw IllegalStateException(response.body.toString())
        }
        return Json.decodeFromString<SessionInfo>(response.text().await())
    }

    private const val TAG = "APIHelper"
}