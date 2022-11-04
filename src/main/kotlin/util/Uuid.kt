package util

object Uuid {
    private var current: Long = 0
    fun next(): Long = current++
}