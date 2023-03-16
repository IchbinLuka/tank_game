package util

import kotlin.math.absoluteValue

interface Scrollable<T> {
    val current: T

    fun next()
    fun previous()

    fun nextWhile(test: (T) -> Boolean) {
        while (test(current)) next()
    }

    fun previousWhile(test: (T) -> Boolean) {
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
        currentIndex = ((currentIndex + 1) % size)
    }

    override fun previous() {
        currentIndex = ((size + currentIndex - 1).absoluteValue % size)
    }

}