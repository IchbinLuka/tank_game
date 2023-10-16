package ext

operator fun Number.minus(other: Number): Number = this.minus(other.toDouble())
operator fun Number.minus(other: Double) = toDouble() - other
operator fun Number.plus(other: Double) = toDouble() + other

fun Number.clamp(min: Number, max: Number): Number = when {
    this.toDouble() < min.toDouble() -> min
    this.toDouble() > max.toDouble() -> max
    else -> this
}