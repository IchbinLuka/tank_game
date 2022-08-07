package util

import kotlin.math.PI
import kotlin.math.atan

fun getLinearGradient(start: Double, end: Double, progress: Double): Double {
    return start + (end - start) * progress
}

fun angleFromAscent(ascent: Double): Double {
    return atan(ascent) + 1.5 *  PI
}