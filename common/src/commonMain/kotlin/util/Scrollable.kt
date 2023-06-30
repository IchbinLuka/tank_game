package util

import kotlin.math.absoluteValue

interface Scrollable<T>: Iterator<T> {
    val current: T

    override fun hasNext(): Boolean = true
    fun previous(): T

    fun nextWhile(test: (T) -> Boolean) {
        while (test(current)) next()
    }

    fun previousWhile(test: (T) -> Boolean) {
        while (test(current)) previous()
    }
}

class ScrollableList<T>(
    elements: Collection<T>
): List<T> by ArrayList<T>(elements = elements), Scrollable<T> {

    private var currentIndex = 0

    override val current: T
        get() = this[currentIndex]

    override fun next(): T {
        currentIndex = ((currentIndex + 1) % size)
        return current
    }

    override fun previous(): T {
        currentIndex = ((size + currentIndex - 1).absoluteValue % size)
        return current
    }

}