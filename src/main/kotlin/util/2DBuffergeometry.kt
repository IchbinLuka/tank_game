package util

import three.js.BufferAttribute
import three.js.BufferGeometry
import three.js.Vector3

class BufferGeometry2Wrapper(
    val geometry: BufferGeometry,
    private val width: Int,
    private val height: Int
) {
    fun vector3At(x: Int, y: Int): Vector3 {
        val index = y * height + x
        val attribute = geometry.getAttribute("position") as BufferAttribute
        return Vector3(
            attribute.getX(index),
            attribute.getY(index),
            attribute.getZ(index)
        )
    }
}