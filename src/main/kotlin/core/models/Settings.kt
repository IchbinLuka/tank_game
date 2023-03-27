package core.models

import kotlinx.browser.localStorage
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.w3c.dom.set

@kotlinx.serialization.Serializable
data class Settings(
    val language: String = "en"
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