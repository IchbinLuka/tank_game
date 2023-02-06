package util

import kotlin.math.absoluteValue

interface Scrollable<T> {
    val current: T

    fun next()
    fun previous()

    fun nextUntil(test: (T) -> Boolean) {
        while (test(current)) next()
    }

    fun previousUntil(test: (T) -> Boolean) {
        while (test(current)) previous()
    }
}

class ScrollableList<T>(
    elements: Collection<T>
): ArrayList<T>(elements = elements), Scrollable<T> {

    private var currentIndex = 0

    override val current: T
        get() = this[currentIndex]

    override fun next() {
        currentIndex = ((currentIndex + 1) % size).absoluteValue
    }

    override fun previous() {
        currentIndex = ((currentIndex - 1) % size).absoluteValue
    }

}