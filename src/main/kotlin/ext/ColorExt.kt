package ext

import three.js.Color

fun Color.toFloatList(): List<Float> = toArray().map { it.toFloat() }
fun Color.toDoubleList(): List<Double> = toArray().map { it.toDouble() }