package gameobjects.bullets

import ext.times
import gameobjects.Tank
import three.js.Vector3

class NoGravityBulletObject(
    startVelocity: Vector3,
    tank: Tank,
) : BulletObject(
    if (startVelocity.length().toDouble() > 20) startVelocity else startVelocity.normalize() * 20.0,
    tank = tank
) {
    override var acceleration: Vector3 = Vector3()

    override fun update(delta: Double) {
        super.update(delta)
        if (position.y > 300) { die() }
    }
}