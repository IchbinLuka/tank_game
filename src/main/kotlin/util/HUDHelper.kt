package util

import kotlinx.browser.document
import org.w3c.dom.Element

object HUDHelper {

    val hudElement: Element? get() = document.getElementById("uiContainer")

    fun resetHUD() {
        val element = hudElement
        while (element?.firstChild != null) {
            element.removeChild(element.firstChild!!)
        }
    }
}