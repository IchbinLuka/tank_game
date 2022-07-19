package util

import kotlin.math.PI
import kotlin.math.atan

fun getLinearGradient(start: Float, end: Float, progress: Float): Float {
    return start + (end - start) * progress
}

fun angleFromAscent(ascent: Float): Float {
    return ((atan(ascent)) + 1.5 *  PI).toFloat()
}