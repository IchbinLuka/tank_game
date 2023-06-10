package services

import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import sessions.HTTP_URL
import sessions.SessionInfo

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

    private const val TAG = "APIHelper"
}