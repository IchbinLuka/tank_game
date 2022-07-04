package extensions

import three.js.Vector3

fun Vector3.toFloatList(): List<Float> {
    return toArray().map { it.toFloat() }
}