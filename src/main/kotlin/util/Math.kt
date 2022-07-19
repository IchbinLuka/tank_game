package util

import kotlin.math.PI

fun toUnitCircle(value: Double): Double {
    val mod = value % (2 * PI)
    return if (mod > 0) mod else 2 * PI + mod
}