package util

import kotlinx.browser.window
import org.w3c.dom.events.KeyboardEvent

object EventManager {

    val keyboardEvents = EventCollector<KeyboardEvent>()

    class EventCollector<T> {
        val events: Array<T> get() = currentBatch.toTypedArray()
        private var currentBatch = mutableListOf<T>()
        private var hiddenBatch = mutableListOf<T>()

        fun addEvent(event: T) {
            hiddenBatch.add(event)
        }

        fun switchBatches() {
            currentBatch = hiddenBatch
            hiddenBatch = mutableListOf()
        }
    }

    init {
        window.onkeydown = {
            keyboardEvents.addEvent(it)
        }
    }
}