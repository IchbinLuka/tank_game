package ext

operator fun Number.minus(other: Number): Number = this.minus(other.toDouble())
operator fun Number.minus(other: Double) = toDouble() - other
operator fun Number.plus(other: Double) = toDouble() + other