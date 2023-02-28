package ext

import three.js.Euler

operator fun Euler.unaryMinus(): Euler = Euler(-x.toDouble(), -y.toDouble(), -z.toDouble())

operator fun Euler.minus(other: Euler): Euler = Euler(x - other.x, y - other.y, z - other.z)