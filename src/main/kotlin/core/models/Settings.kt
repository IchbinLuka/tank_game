package core.models

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import util.CookieUtil

@kotlinx.serialization.Serializable
data class Settings(
    val language: String = "en"
) {
    fun saveToCookies() {
        CookieUtil.setCookie("settings", Json.encodeToString(this))
    }

    companion object {
        fun fromCookies(): Settings? = CookieUtil.loadCookie("settings")?.let {
            try {
                Json.decodeFromString(it)
            } catch (e: Exception) {
                null
            }
        }
    }
}