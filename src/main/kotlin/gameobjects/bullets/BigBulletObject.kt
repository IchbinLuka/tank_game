package gameobjects.bullets

import ext.times
import gameobjects.Tank
import three.js.Vector3

class BigBulletObject(
    startVelocity: Vector3,
    tank: Tank
) : BulletObject(startVelocity * 0.6, tank) {
    override val bulletRadius: Float = 1f

    override val explosionWidth: Double = 60.0

    override val damage: Double = 0.7
}