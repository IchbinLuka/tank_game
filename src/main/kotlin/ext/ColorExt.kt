package ext

import three.js.Color
import three.js.Vector3

fun Color.toFloatList(): List<Float> = toArray().map { it.toFloat() }
fun Color.toDoubleList(): List<Double> = toArray().map { it.toDouble() }

fun Color.toVector3(): Vector3 = Vector3(r, g, b)