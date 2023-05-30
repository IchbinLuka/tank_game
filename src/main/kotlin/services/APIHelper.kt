package services

import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromDynamic
import sessions.Session
import util.Log
import kotlin.reflect.typeOf

object APIHelper {

    suspend fun fetchSessions(): Collection<Session> {
        val response = window.fetch("http://localhost:8081/sessions").await()
        if (response.status != 200.toShort()) {
            throw IllegalStateException(response.body.toString())
        }
        return Json.decodeFromString<List<Session>>(response.text().await())
    }

    private const val TAG = "APIHelper"
}