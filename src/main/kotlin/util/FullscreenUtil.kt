package util

import kotlinx.browser.document

object FullscreenUtil {
    fun setFullscreen(value: Boolean) {
        if (value) {
            document.body?.requestFullscreen()
        } else {
            document.exitFullscreen()
        }
    }
}