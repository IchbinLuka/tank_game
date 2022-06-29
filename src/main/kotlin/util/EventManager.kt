package util

import kotlinx.browser.window
import org.w3c.dom.events.KeyboardEvent

object EventManager {

    val listeners = mutableSetOf<(KeyboardEvent) -> Unit>()

}