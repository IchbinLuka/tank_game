package util

object Uuid {
    private var current: Long = 0

    /** Returns a new value guaranteed to be different from all previous */
    fun next(): Long = current++
}