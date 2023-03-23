package util

import kotlinx.browser.document
import types.decodeURIComponent
import types.encodeURIComponent
import kotlin.js.Date

object CookieUtil {
    fun loadCookie(key: String): String? {
        val name = "$key="
        val cookies = document.cookie.split(";").map { it.trim() }
        for (cookie in cookies) {
            if (cookie.startsWith(name)) {
                return decodeURIComponent(cookie.substring(name.length))
            }
        }
        return null
    }

    fun setCookie(key: String, value: String, expirationDays: Int = 30) {
        val expirationDate = Date(Date().getTime() + expirationDays * 24 * 60 * 60 * 1000)
        document.cookie = "$key=${encodeURIComponent(value)}; expiryDate=${expirationDate.toUTCString()}; samesite"
    }
}