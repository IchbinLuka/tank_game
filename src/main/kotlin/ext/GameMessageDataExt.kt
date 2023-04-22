package ext

import ColorData
import GameMessageData
import three.js.Color
import three.js.Vector3

fun GameMessageData.Vec3.Companion.fromVector3(v: Vector3): GameMessageData.Vec3 = GameMessageData.Vec3(
    v.x,
    v.y,
    v.z
)

fun GameMessageData.Vec3.toVector3(): Vector3 = Vector3(x, y, z)

fun ColorData.toColor(): Color = Color(r, g, b)