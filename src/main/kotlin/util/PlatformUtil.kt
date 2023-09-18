package util

import kotlinx.browser.window

object PlatformUtil {
    val isMobile: Boolean
        get() = "(iphone)|(android)|(blackberry)".toRegex().containsMatchIn(window.navigator.userAgent.lowercase())
}