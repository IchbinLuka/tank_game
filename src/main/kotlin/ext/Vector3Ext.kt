package ext

import three.js.Vector3
import kotlin.math.pow
import kotlin.math.sqrt

fun Vector3.toFloatList(): List<Float> = toArray().map { it.toFloat() }
fun Vector3.toFloatArray(): Array<Float> = toFloatList().toTypedArray()

fun Vector3.set(value: Vector3) = set(*value.toFloatArray())

operator fun Vector3.times(number: Number): Vector3 = Vector3().addScaledVector(this, number)

operator fun Vector3.plusAssign(vector3: Vector3) { add(vector3) }

operator fun Vector3.plus(vector3: Vector3): Vector3 = clone().add(vector3)

operator fun Vector3.minus(vector: Vector3): Vector3 = clone().addScaledVector(vector, -1)

operator fun Vector3.unaryMinus(): Vector3 = this * -1

fun Vector3.euclidicNorm(): Double = sqrt(x.pow(2) + y.pow(2) + z.pow(2))