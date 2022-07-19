package extensions

import ext.minus

operator fun Number.minus(other: Number): Number {
    return this.minus(other.toDouble())
}