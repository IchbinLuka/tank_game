package core.models

import kotlinx.browser.localStorage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.w3c.dom.set
import sessions.DEFAULT_HOST
import sessions.PORT

@kotlinx.serialization.Serializable
data class Settings(
    val language: String = "en",
    val server: String = "$DEFAULT_HOST:$PORT"
) {
    fun saveToCookies() {
        localStorage["settings"] = Json.encodeToString(this)
    }

    companion object {
        fun fromCookies(): Settings? = localStorage.getItem("settings")?.let {
            try {
                Json.decodeFromString(it)
            } catch (e: Exception) {
                null
            }
        }
    }
}