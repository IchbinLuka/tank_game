package ext

import three.js.Vector3

fun Vector3.toFloatList(): List<Float> = toArray().map { it.toFloat() }
fun Vector3.toFloatArray(): Array<Float> = toFloatList().toTypedArray()

fun Vector3.set(value: Vector3) = set(*value.toFloatArray())

operator fun Vector3.times(number: Number): Vector3 = Vector3().addScaledVector(this, number)

operator fun Vector3.plusAssign(vector3: Vector3) { add(vector3) }

operator fun Vector3.plus(vector3: Vector3): Vector3 = clone().add(vector3)