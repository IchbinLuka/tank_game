package ext

import three.js.Color

fun Color.toFloatList(): List<Float> = toArray().map { it.toFloat() }