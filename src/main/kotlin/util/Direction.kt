package util

import three.js.Vector3

enum class Direction(val vector3: Vector3) {
    Left(Vector3(-1, 0, 0)),
    Right(Vector3(1, 0, 0)),
    Up(Vector3(0, 1, 0)),
    Down(Vector3(0, -1, 0))
}